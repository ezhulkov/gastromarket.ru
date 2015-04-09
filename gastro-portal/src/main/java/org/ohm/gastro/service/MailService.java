package org.ohm.gastro.service;

import org.springframework.mail.MailException;

import java.util.Map;

/**
 * Created by ezhulkov on 13.03.15.
 */
public interface MailService {

    String CHANGE_PASSWD = "change_passwd";
    String NEW_CATALOG = "new_catalog";
    String NEW_USER = "new_user";
    String NEW_APPLICATION = "new_application";
    String NEW_APPLICATION_COOK = "new_application_cook";
    String FEEDBACK = "feedback";
    String NEW_ORDER_ADMIN = "new_order_admin";
    String NEW_ORDER_COOK = "new_order_cook";
    String NEW_ORDER_CUSTOMER = "new_order_customer";
    String EDIT_ORDER = "change_order";

    void sendMailMessage(String recipient, String templateKey, Map<String, Object> params) throws MailException;

    void sendAdminMessage(String templateKey, Map<String, Object> params) throws MailException;

}
