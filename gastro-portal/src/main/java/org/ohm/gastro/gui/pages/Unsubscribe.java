package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Unsubscribe extends BaseComponent {

    @Property
    private UserEntity recipient;

    public void onActivate(String link) {
        this.recipient = getMailService().parseSecuredEmail(link);
        if (recipient != null) {
            logger.info("Unsubscribing {}", recipient);
            recipient.setSubscribeEmail(false);
            getUserService().saveUser(recipient);
        }
    }

}
