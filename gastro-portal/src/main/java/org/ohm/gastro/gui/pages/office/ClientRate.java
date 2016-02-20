package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.pages.AbstractPage;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class ClientRate extends AbstractPage {

    @Property
    private OrderEntity order;

    public void onActivate(Long id) {
        order = getOrderService().findOrder(id);
    }

    public Long onPassivate() {
        return order == null ? null : order.getId();
    }

}
