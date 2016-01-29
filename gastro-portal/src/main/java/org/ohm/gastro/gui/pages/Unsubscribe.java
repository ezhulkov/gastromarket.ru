package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.util.CommonsUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Unsubscribe extends BaseComponent {

    @Property
    private String reason;

    @Property
    private boolean client;

    @Property
    private boolean spam;

    @Property
    private boolean many;

    @Property
    private boolean wrong;

    @Property
    private String email;

    public void onActivate(String link) {
        this.email = CommonsUtils.parseSecuredEmail(link).orElse(null);
    }

    public Object onSubmitFromForm(String email) {
        UserEntity recipient = getUserService().findUser(email);
        if (recipient != null) {
            logger.info("Unsubscribing {}", recipient);
            final Map<String, Object> params = new HashMap<String, Object>() {
                {
                    put("email", recipient.getEmail());
                    put("reason", reason);
                    put("client", client);
                    put("spam", spam);
                    put("wrong", wrong);
                    put("many", many);
                }
            };
            getMailService().sendAdminMessage(MailService.MailType.USER_UNSUBSCRIBED, params);
            recipient.setSubscribeEmail(false);
            getUserService().saveUser(recipient);
            return getPageLinkSource().createPageRenderLinkWithContext(UnsubscribeRes.class, recipient.getEmail());
        }
        return Index.class;
    }

}
