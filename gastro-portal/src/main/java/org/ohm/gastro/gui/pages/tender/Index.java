package org.ohm.gastro.gui.pages.tender;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Cookies;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Index extends BaseComponent {

    @Property
    private OrderEntity order;

    @Property
    private boolean newOrder;

    @Inject
    @Property
    private Block orderBlock;

    @Inject
    private Cookies cookies;

    public Object onActivate() {
        return onActivate(null);
    }

    public Object onActivate(Long orderId, boolean newOrder) {
        order = getOrderService().findOrder(orderId);
        if (order == null) return new HttpError(404, "Page not found.");
        this.newOrder = !isCook() && newOrder;
        final String cookieName = "tender_seen_" + order.getId();
        if (cookies.readCookieValue(cookieName) == null) {
            cookies.writeCookieValue(cookieName, "true");
            order.setViewsCount(order.getViewsCount() + 1);
            getOrderService().saveOrder(order);
        }
        return true;
    }

    public Object onActivate(Long orderId) {
        return onActivate(orderId, false);
    }

    public Object[] onPassivate() {
        return newOrder ? new Object[]{order.getId(), newOrder} : new Object[]{order.getId()};
    }

    public String getKeywords() {
        return getMessages().format("page.keywords.tender", order.getName());
    }

}
