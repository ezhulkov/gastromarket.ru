package org.ohm.gastro.gui.pages.office;

import com.google.common.collect.Lists;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.dto.Breadcrumb;
import org.ohm.gastro.gui.pages.AbstractPage;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Order extends AbstractPage {

    @Property
    private OrderEntity order;

    @Property
    private boolean privateOrders;

    @Property
    private boolean newOrder;

    @Inject
    @Property
    private Block orderBlock;

    public boolean onActivate(boolean privateOrders, Long orderId, boolean newOrder) {
        this.order = getOrderService().findOrder(orderId);
        this.newOrder = !isCook() && newOrder;
        this.privateOrders = privateOrders;
        return true;
    }

    public boolean onActivate(boolean privateOrders, Long orderId) {
        return onActivate(privateOrders, orderId, false);
    }

    public Object[] onPassivate() {
        return newOrder ? new Object[]{privateOrders, order.getId(), newOrder} : new Object[]{privateOrders, order.getId()};
    }

    @Override
    public String getTitle() {
        return order == null ? "" : order.isTender() ? order.getName() : order.getOrderNumber();
    }

    @Override
    public java.util.List<Breadcrumb> getBreadcrumbsContext() {
        return Lists.newArrayList(mainPage,
                                  Breadcrumb.of(getMessages().get(Orders.class.getName()), Orders.class),
                                  Breadcrumb.of(getTitle(), this.getClass())
        );
    }

}
