package org.ohm.gastro.gui.pages.tender;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
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

    public boolean onActivate(Long orderId, boolean newOrder) {
        this.order = getOrderService().findOrder(orderId);
        this.newOrder = !isCook() && newOrder;
        return true;
    }

    public boolean onActivate(Long orderId) {
        return onActivate(orderId, false);
    }

    public Object[] onPassivate() {
        return newOrder ? new Object[]{order.getId(), newOrder} : new Object[]{order.getId()};
    }

}
