package org.ohm.gastro.gui.components.order;

import org.ohm.gastro.domain.OrderEntity.Status;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class Payment extends AbstractOrder {

    public void onActionFromPrepay(Long oId) {
        this.order = getOrderService().findOrder(oId);
        this.catalog = order.getCatalog();
        getOrderService().changeStatus(order, Status.PAID, catalog, getAuthenticatedUser());
    }

}
