package org.ohm.gastro.service;

import org.ohm.gastro.domain.UserEntity;
import org.springframework.mail.MailException;

import javax.annotation.Nonnull;
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
    String NEW_TENDER_ADMIN = "new_tender_admin";
    String NEW_TENDER_COOK = "new_tender_cook";
    String NEW_TENDER_CUSTOMER = "new_tender_customer";
    String TENDER_ATTACHED_COOK = "tender_attached_cook";
    String TENDER_ATTACHED_CUSTOMER = "tender_attached_customer";
    String NEW_ORDER_ADMIN = "new_order_admin";
    String NEW_ORDER_COOK = "new_order_cook";
    String NEW_ORDER_CUSTOMER = "new_order_customer";
    String ORDER_COMMENT = "order_comment";
    String EDIT_ORDER = "edit_order";

    String MC_CATALOG = "CATALOG";
    String MC_FNAME = "FNAME";
    String MC_SOURCE = "SOURCE";
    String MC_PASSWORD = "PASSWORD";

    void sendMailMessage(final String recipient, final String templateKey, Map<String, Object> params) throws MailException;

    void sendAdminMessage(final String templateKey, final Map<String, Object> params) throws MailException;

    void syncChimpList(@Nonnull final UserEntity user, @Nonnull final Map<String, String> mergeVars);

    void deleteChimpList(@Nonnull final UserEntity user);

}
