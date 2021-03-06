package org.ohm.gastro.service.impl;

import com.google.common.base.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.ohm.gastro.domain.NotificationConfigEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.NotificationConfigRepository;
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.trait.Logging;
import org.ohm.gastro.util.CommonsUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 13.03.15.
 */
@Component
public class MailServiceImpl implements MailService, Logging {

    private final static Pattern TITLE_CUTTER = Pattern.compile("\\<title\\>(.*)\\<\\/title\\>");
    private final static String SMS_TEMPLATE = "https://smsc.ru/sys/send.php?login=ezhulkov&psw=27bc90fc0cd87a2a111f195797c045cb&phones=%s&mes=%s&tinyurl=1&time=9-21&sender=GastroMarke&charset=utf-8";
    private final static String TEMPLATE_PATH = "email/%s.vm";
    private final static String MERGE_PAIR = "\"%s\":\"%s\"";
    private final static String MC_SYNC_ENDPOINT = "https://us11.api.mailchimp.com/2.0/lists/batch-subscribe";
    private final static String MC_UNSUB_ENDPOINT = "https://us11.api.mailchimp.com/2.0/lists/unsubscribe";
    private final static String MC_SUB_ENDPOINT = "https://us11.api.mailchimp.com/2.0/lists/subscribe";
    private final static String MC_SUBSCRIBE_BLOCK =
            "{" +
                    "  \"apikey\": \"c1a3fcb063adeab16e8d2be9e09b8e97-us11\"," +
                    "  \"id\": \"%s\"," +
                    "  \"batch\": [{" +
                    "       \"email\": {" +
                    "           \"email\": \"%s\"," +
                    "           \"euid\": \"%s\"," +
                    "           \"luid\": \"%s\"" +
                    "       }," +
                    "       \"email_type\": \"html\"," +
                    "       \"merge_vars\": {%s}" +
                    "  }]," +
                    "  \"double_optin\": false," +
                    "  \"update_existing\": true," +
                    "  \"replace_interests\": true" +
                    "}";
    private final static String MC_UNSUB_SUB_BLOCK =
            "{" +
                    "  \"apikey\": \"c1a3fcb063adeab16e8d2be9e09b8e97-us11\"," +
                    "  \"id\": \"%s\"," +
                    "  \"email\": {" +
                    "    \"email\": \"%s\"," +
                    "    \"euid\": \"%s\"," +
                    "    \"luid\": \"%s\"" +
                    "   }" +
                    "}";

    private final VelocityEngine velocityEngine;
    private final String defaultFrom;
    private final ExecutorService executorService;
    private final NotificationConfigRepository notificationConfigRepository;
    private final JavaMailSenderImpl mailSender;
    private final boolean production;
    private final Map<String, ScheduledFuture> scheduledFutures = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    public MailServiceImpl(@Value("${mail.from:ГастроМаркет <contacts@gastromarket.ru>}") String defaultFrom,
                           @Value("${production}") boolean production,
                           final NotificationConfigRepository notificationConfigRepository) throws Exception {
        this.notificationConfigRepository = notificationConfigRepository;
        final Properties properties = new Properties();
        properties.setProperty("input.encoding", "UTF-8");
        properties.setProperty("resource.loader", "class");
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        properties.setProperty("velocimacro.library", "");
        properties.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        properties.put("runtime.log.logsystem.log4j.category", "velocity");
        properties.put("runtime.log.logsystem.log4j.logger", "velocity");
        this.velocityEngine = new VelocityEngine(properties);
        this.defaultFrom = defaultFrom;
        this.executorService = Executors.newFixedThreadPool(16);
        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("localhost");
        mailSender.setDefaultEncoding("UTF-8");
        this.mailSender = mailSender;
        this.production = production;
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdownNow();
        scheduledExecutorService.shutdownNow();
    }

    @Override
    public void sendMailMessage(final UserEntity recipient, final MailType mailType, final Map<String, Object> params) throws MailException {
        if (!isNotificationEnabled(recipient, mailType)) {
            logger.info("Skipping email to " + recipient);
            return;
        }
        sendMailMessage(recipient.getEmail(), mailType, params);
    }

    @Override
    public void sendSmsMessage(final UserEntity customer, final SmsType smsType, final Map<String, Object> params) {
        if (StringUtils.isNoneEmpty(customer.getMobilePhone())) {
            try (
                    final StringWriter stringWriter = new StringWriter()
            ) {
                velocityEngine.mergeTemplate(String.format(TEMPLATE_PATH, smsType.getTemplate()), "UTF-8", new VelocityContext(new HashMap<>(params)), stringWriter);
                final String smsUrl = String.format(SMS_TEMPLATE, URLEncoder.encode(customer.getMobilePhone()), URLEncoder.encode(stringWriter.toString(), "UTF-8"));
                logger.info("Sending sms to {}", smsUrl);
                final HttpURLConnection urlConnection = (HttpURLConnection) new URL(smsUrl).openConnection();
                logger.info("Sms response code {}", urlConnection.getResponseCode());
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    @Override
    public void sendMailMessage(final String recipient, final MailType mailType, final Map<String, Object> params) {

        logger.info("Sending email to " + recipient + " using template " + mailType);

        params.put("unsubscribe", CommonsUtils.generateSecuredEmail(recipient));
        params.put("quicklogin", CommonsUtils.generateSecuredEmail(recipient, TimeUnit.HOURS.toMillis(24)));

        try (
                final StringWriter stringWriter = new StringWriter()
        ) {
            velocityEngine.mergeTemplate(String.format(TEMPLATE_PATH, mailType.getTemplate()), "UTF-8", new VelocityContext(new HashMap<>(params)), stringWriter);
            final String messageBody = stringWriter.toString();
            final String title = getTitle(messageBody);

            FileUtils.write(new File("/tmp/" + mailType.getTemplate() + ".html"), messageBody);

            if (production) {
                final MimeMessagePreparator preparator = mimeMessage -> {
                    MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false);
                    message.setText(messageBody, true);
                    message.setSubject(title == null ? "ГастроМаркет" : title);
                    message.setReplyTo(defaultFrom);
                    message.setFrom(defaultFrom);
                    message.setTo(recipient);
                };
                executorService.execute(() -> {
                    try {
                        synchronized (mailSender) {
                            mailSender.send(preparator);
                        }
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                });
                logger.debug("Email was sent");
            }
        } catch (Exception e) {
            logger.error("", e);
        }

    }

    @Override
    public void sendAdminMessage(final MailType mailType, final Map<String, Object> params) throws MailException {
        sendMailMessage("contacts@gastromarket.ru", mailType, params);
    }

    @Override
    public void syncChimpList(@Nonnull String email, @Nonnull Map<String, String> mergeVars, String listId) {
        if (!production) return;
        final String lCEmail = email.toLowerCase();
        final String mergeVarsStr = mergeVars.entrySet().stream()
                .filter(t -> StringUtils.isNotEmpty(t.getValue()))
                .map(t -> String.format(MERGE_PAIR, t.getKey(), t.getValue()))
                .collect(Collectors.joining(","));
        final String mailChimpJson = String.format(MC_SUBSCRIBE_BLOCK,
                                                   listId,
                                                   lCEmail,
                                                   lCEmail,
                                                   lCEmail,
                                                   mergeVarsStr);
        logger.debug("MailChimp request {}", mailChimpJson);
        syncMailChimp(MC_SYNC_ENDPOINT, new StringEntity(mailChimpJson, Charsets.UTF_8));
    }

    @Override
    public void unsubscribeChimpList(@Nonnull final String email, @Nonnull final String listId) {
        subUnsub(email, listId, MC_UNSUB_ENDPOINT);
    }

    @Override
    public void subscribeChimpList(@Nonnull final String email, @Nonnull final String listId) {
        subUnsub(email, listId, MC_SUB_ENDPOINT);
    }


    @Override
    public void scheduleSend(final UserEntity user, final MailType mailType, final Consumer<String> routine, final long time, final TimeUnit timeUnit) {
        if (!isNotificationEnabled(user, mailType)) return;
        logger.info("Scheduling task for {}, key {}", user, mailType);
        final String operationKey = String.format("%s%s", user.getId(), mailType);
        synchronized (scheduledFutures) {
            final ScheduledFuture scheduledFuture = scheduledFutures.remove(operationKey);
            if (scheduledFuture != null) scheduledFuture.cancel(true);
            final Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
            scheduledFutures.put(operationKey,
                                 scheduledExecutorService.schedule(() -> {
                                     MDC.setContextMap(copyOfContextMap);
                                     synchronized (scheduledFutures) {
                                         if (scheduledFutures.remove(operationKey) == null) return;
                                         routine.accept(operationKey);
                                     }
                                     MDC.clear();
                                 }, time, timeUnit));
        }
    }

    @Override
    public void cancelAllTasks(final UserEntity user) {
        if (scheduledFutures.remove(String.format("%s%s", user.getId(), MailType.NEW_MESSAGE)) != null) {
            logger.info("Cancelling new_message scheduled tasks for {}", user);
        }
    }

    @Override
    public boolean isNotificationEnabled(UserEntity user, MailType mailType) {
        if (user == null || mailType == null || !user.isSubscribeEmail()) return false;
        if (user.getId() == null) return true;
        final NotificationConfigEntity config = notificationConfigRepository.findByUserAndMailType(user, mailType);
        return config == null || config.isEnabled();
    }

    private void syncMailChimp(String mcSyncEndpoint, StringEntity entity) {
        try (
                final CloseableHttpClient httpClient = HttpClients.createDefault()
        ) {
            final HttpPost httpPost = new HttpPost(mcSyncEndpoint);
            httpPost.setEntity(entity);
            httpClient.execute(httpPost);
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    private String getTitle(String messageBody) {
        Matcher matcher = TITLE_CUTTER.matcher(messageBody);
        if (matcher.find()) return matcher.group(1);
        return null;
    }

    private void subUnsub(String email, String listId, String endPoint) {
        if (!production) return;
        final String lCEmail = email.toLowerCase();
        final String mailChimpJson = String.format(MC_UNSUB_SUB_BLOCK,
                                                   listId,
                                                   lCEmail,
                                                   lCEmail,
                                                   lCEmail);
        logger.debug("MailChimp request {}", mailChimpJson);
        syncMailChimp(endPoint, new StringEntity(mailChimpJson, Charsets.UTF_8));
    }

}
