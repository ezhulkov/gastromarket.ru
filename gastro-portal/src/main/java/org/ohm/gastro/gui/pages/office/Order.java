package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Order extends BaseComponent {

    @Parameter
    private Long orderId;

    @Property
    private OrderEntity order;

    public void beginRender() {
        this.order = getOrderService().findOrder(orderId);
    }

}
