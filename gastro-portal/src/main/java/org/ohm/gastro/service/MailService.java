package org.ohm.gastro.service;

import org.ohm.gastro.domain.UserEntity;
import org.springframework.mail.MailException;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by ezhulkov on 13.03.15.
 */
public interface MailService {

    enum MailType {

        NEW_BILL_ADMIN("new_bill_admin"),
        CATALOG_RATE("catalog_rate", true),
        USER_RATE("user_rate"),
        CHANGE_PASSWD("change_passwd"),
        NEW_CATALOG("new_catalog"),
        NEW_USER("new_user"),
        NEW_APPLICATION("new_application"),
        NEW_APPLICATION_COOK("new_application_cook"),
        FEEDBACK("feedback"),
        NEW_TENDER_ADMIN("new_tender_admin"),
        NEW_TENDER_COOK("new_tender_cook", true),
        NEW_TENDER_CUSTOMER("new_tender_customer"),
        TENDER_ATTACHED_COOK("tender_attached_cook", true),
        TENDER_ATTACHED_CUSTOMER("tender_attached_customer"),
        TENDER_ATTACHED_ADMIN("tender_attached_admin"),
        NEW_ORDER_ADMIN("new_order_admin"),
        NEW_ORDER_COOK("new_order_cook", true),
        NEW_ORDER_CUSTOMER("new_order_customer"),
        CLOSE_ORDER_COOK("close_order_cook", true),
        CLOSE_ORDER_CUSTOMER("close_order_customer"),
        CLOSE_ORDER_ADMIN("close_order_admin"),
        ORDER_COMMENT("order_comment"),
        ORDER_COMMENT_ADMIN("order_comment_admin"),
        NEW_MESSAGE("new_message", true),
        EDIT_ORDER("edit_order"),
        TENDER_REMINDER("tender_reminder"),
        ORDER_RATE_REMINDER("order_rate_reminder"),
        ORDER_READY_REMINDER("order_ready_reminder"),
        ORDER_CLOSE_REMINDER("order_close_reminder"),
        TENDER_EXPIRED_SURVEY("tender_expired_survey"),
        USER_UNSUBSCRIBED("user_unsubscribed");

        private final String template;
        private final boolean configurable;

        MailType(String template, boolean configurable) {
            this.template = template;
            this.configurable = configurable;
        }

        MailType(String template) {
            this.template = template;
            this.configurable = false;
        }

        public String getTemplate() {
            return template;
        }

        public boolean isConfigurable() {
            return configurable;
        }

    }

    String MC_FILLED = "FILLED";
    String MC_REGION = "REGION";
    String MC_CATALOG = "CATALOG";
    String MC_FNAME = "FNAME";
    String MC_SOURCE = "SOURCE";
    String MC_PASSWORD = "PASSWORD";

    void sendMailMessage(final String recipient, final MailType mailType, Map<String, Object> params) throws MailException;

    void sendMailMessage(final UserEntity recipient, final MailType mailType, Map<String, Object> params) throws MailException;

    void sendAdminMessage(final MailType mailType, final Map<String, Object> params) throws MailException;

    void syncChimpList(@Nonnull final UserEntity user, @Nonnull final Map<String, String> mergeVars);

    void deleteChimpList(@Nonnull final UserEntity user);

    void scheduleSend(UserEntity user, MailType mailType, Consumer<String> routine, long time, TimeUnit timeUnit);

    void cancelAllTasks(UserEntity user);

    boolean isNotificationEnabled(UserEntity user,MailType mailType);

}
