package org.ohm.gastro.service.impl;

import com.google.common.base.Charsets;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 13.03.15.
 */
@Component
public class MailServiceImpl implements MailService, Logging {

    private final static Pattern TITLE_CUTTER = Pattern.compile("\\<title\\>(.*)\\<\\/title\\>");
    private final static String TEMPLATE_PATH = "email/%s.vm";
    private final static String MERGE_PAIR = "\"%s\":\"%s\"";
    private final static String MC_ENDPOINT = "https://us11.api.mailchimp.com/2.0/lists/batch-subscribe";
    private final static String MC_SUBSCRIBE_BLOCK =
            "{" +
                    "  \"apikey\": \"c1a3fcb063adeab16e8d2be9e09b8e97-us11\"," +
                    "  \"id\": \"122757a479\"," +
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

    private final VelocityEngine velocityEngine;
    private final String defaultFrom;
    private final ExecutorService executorService;
    private final JavaMailSenderImpl mailSender;
    private final boolean production;

    @Autowired
    public MailServiceImpl(@Value("${mail.from:ГастроМаркет <contacts@gastromarket.ru>}") String defaultFrom,
                           @Value("${production}") boolean production) {
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
    }

    @Override
    public void sendMailMessage(String recipient, String templateKey, Map<String, Object> params) {

        if (!production) return;

        logger.info("Sending email to " + recipient + " using template " + templateKey);

        try (
                final StringWriter stringWriter = new StringWriter();
        ) {
            velocityEngine.mergeTemplate(String.format(TEMPLATE_PATH, templateKey), "UTF-8", new VelocityContext(new HashMap<>(params)), stringWriter);
            final String messageBody = stringWriter.toString();
            final String title = getTitle(messageBody);
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
        } catch (Exception e) {
            logger.error("", e);
        }


    }

    @Override
    public void sendAdminMessage(final String templateKey, final Map<String, Object> params) throws MailException {
        if (!production) return;
        sendMailMessage("contacts@gastromarket.ru", templateKey, params);
    }

    @Override
    public void syncChimpList(@Nonnull UserEntity user, @Nonnull Map<String, String> mergeVars) {
        if (!production) return;
        final String mergeVarsStr = mergeVars.entrySet().stream()
                .filter(t -> StringUtils.isNotEmpty(t.getValue()))
                .map(t -> String.format(MERGE_PAIR, t.getKey(), t.getValue()))
                .collect(Collectors.joining(","));
        final String mailChimpJson = String.format(MC_SUBSCRIBE_BLOCK,
                                                   user.getEmail(),
                                                   user.getEmail(),
                                                   user.getEmail(),
                                                   mergeVarsStr);
        logger.info("MailChimp request {}", mailChimpJson);
        try (
                final CloseableHttpClient httpClient = HttpClients.createDefault();
        ) {
            final HttpPost httpPost = new HttpPost(MC_ENDPOINT);
            httpPost.setEntity(new StringEntity(mailChimpJson, Charsets.UTF_8));
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

}
