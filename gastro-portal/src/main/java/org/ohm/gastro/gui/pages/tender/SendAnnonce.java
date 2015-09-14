package org.ohm.gastro.gui.pages.tender;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class SendAnnonce extends BaseComponent {

    @Property
    private OrderEntity order;

    @Property
    private boolean sent;

    public void onActivate(Long orderId) {
        this.order = getOrderService().findOrder(orderId);
        sent = !order.isAnnonceSent();
        getOrderService().sendTenderAnnonce(order);
    }

}
