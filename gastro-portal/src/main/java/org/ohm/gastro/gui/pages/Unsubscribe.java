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
        this.recipient = getMailService().parseUnsubscribeLink(link);
        if (recipient != null) {
            recipient.setSubscribeEmail(false);
            getUserService().saveUser(recipient);
        }
    }

}
