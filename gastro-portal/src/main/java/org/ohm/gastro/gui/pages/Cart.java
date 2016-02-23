package org.ohm.gastro.gui.pages;

import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.gui.pages.office.order.List;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Cart extends AbstractPage {

    public Object onActivate() {
        if (isAuthenticated()) return getPageLinkSource().createPageRenderLinkWithContext(List.class, true, Status.NEW);
        return true;
    }

}
