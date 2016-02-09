package org.ohm.gastro.gui.pages;

import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.gui.pages.office.Orders;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Cart extends AbstractPage {

    public Object onActivate() {
        if (isAuthenticated()) return getPageLinkSource().createPageRenderLinkWithContext(Orders.class, true, Status.NEW);
        return true;
    }

}
