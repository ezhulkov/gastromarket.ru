package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class HeaderLayout extends BaseComponent {

    @Inject
    @Property
    private Block basketBlock;

    public void beginRender() {
        getShoppingCart().setBasketBlock(basketBlock);
    }

    public int getUnreadMessages() {
        return getMessageService().getUnreadMessages(getAuthenticatedUser());
    }

    public String getFloatClass() {
        return "";
    }

}
