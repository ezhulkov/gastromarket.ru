package org.ohm.gastro.gui.pages.admin.orders;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class List extends BaseComponent {

    @Property
    private OrderEntity oneOrder;

    public java.util.List<OrderEntity> getOrders() {
        return getOrderService().findAllOrders();
    }

    public String getStatus() {
        return getMessages().get(oneOrder.getStatus().name());
    }

}
