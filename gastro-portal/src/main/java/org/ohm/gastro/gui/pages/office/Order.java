package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Order extends BaseComponent {

    @Property
    private OrderEntity order;

    @Property
    private boolean newOrder;

    @Inject
    @Property
    private Block orderBlock;

    public boolean onActivate(Long orderId, boolean newOrder) {
        this.order = getOrderService().findOrder(orderId);
        this.newOrder = newOrder;
        return true;

    }

    public boolean onActivate(Long orderId) {
        return onActivate(orderId, false);
    }

    public Object[] onPassivate() {
        return new Object[]{order.getId(), newOrder};
    }

}
