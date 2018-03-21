package org.ohm.gastro.gui.pages.catalog;

import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.pages.AbstractPage;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class OrderResults extends AbstractPage {

    private OrderEntity order;

    public void onActivate(Long id) {
        order = getOrderService().findOrder(id);
    }

    public Long onPassivate() {
        return order == null ? null : order.getId();
    }

    public Object[] getContext() {
        return new Object[]{order.getCustomer().getId(), order.getCatalog().getUser().getId()};
    }

}
