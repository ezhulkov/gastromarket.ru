package org.ohm.gastro.service;

import org.springframework.mail.MailException;

import java.util.Map;

/**
 * Created by ezhulkov on 13.03.15.
 */
public interface MailService {

    public static final String CHANGE_PASSWD = "change_passwd";
    public static final String NEW_APPLICATION = "new_application";
    public static final String FEEDBACK = "feedback";

    public void sendMailMessage(String recipient, String templateKey, Map<String, String> params) throws MailException;

    public void sendAdminMessage(String templateKey, Map<String, String> params) throws MailException;

}
