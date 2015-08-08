package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Order extends BaseComponent {

    @Property
    private OrderEntity order;

    @Property
    private boolean newOrder = false;

    public void onActivate(Long orderId, boolean newOrder) {
        this.order = getOrderService().findOrder(orderId);
        this.newOrder = newOrder;
    }

    public void onActivate(Long orderId) {
        onActivate(orderId, false);
    }

    public Object[] onPassivate() {
        return new Object[]{order.getId(), newOrder};
    }


}
