package org.ohm.gastro.service.impl;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ezhulkov on 13.03.15.
 */
@Component
public class MailServiceImpl implements MailService, Logging {

    private static final Pattern TITLE_CUTTER = Pattern.compile("\\<title\\>(.*)\\<\\/title\\>");
    private final static String templatePath = "email/%s.html";

    private final VelocityEngine velocityEngine;
    private final String defaultFrom;
    private final ExecutorService executorService;
    private final JavaMailSenderImpl mailSender;

    @Autowired
    public MailServiceImpl(@Value("${mail.from:contacts@gastromarket.ru}") String defaultFrom) {
        final Properties properties = new Properties();
        properties.setProperty("resource.loader", "class");
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        properties.setProperty("velocimacro.library", "");
        this.velocityEngine = new VelocityEngine(properties);
        this.defaultFrom = defaultFrom;
        this.executorService = Executors.newFixedThreadPool(5);
        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("localhost");
        mailSender.setDefaultEncoding("UTF-8");
        this.mailSender = mailSender;
    }

    @Override
    public void sendMailMessage(String recipient, String templateKey, HashMap<String, Object> params) {

        logger.info("Sending email to " + recipient + " using template " + templateKey);

        try (
                final StringWriter stringWriter = new StringWriter();
        ) {
            final String template = IOUtils.toString(getClass().getResourceAsStream(String.format(templatePath, templateKey)));
            velocityEngine.evaluate(new VelocityContext(params), stringWriter, "LOG", template);
            final String messageBody = stringWriter.toString();
            final String title = getTitle(messageBody);
            final MimeMessagePreparator preparator = mimeMessage -> {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false);
                message.setText(messageBody, true);
                message.setSubject(title == null ? "Gastromarket.ru" : title);
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

    private String getTitle(String messageBody) {
        Matcher matcher = TITLE_CUTTER.matcher(messageBody);
        if (matcher.find()) return matcher.group(1);
        return null;
    }

}
