package org.ohm.gastro.service;

import org.springframework.mail.MailException;

import java.util.HashMap;

/**
 * Created by ezhulkov on 13.03.15.
 */
public interface MailService {

    public static final String CHANGE_PASSWD = "change_passwd";

    public void sendMailMessage(String recipient, String templateKey, HashMap<String, Object> params) throws MailException;

}
