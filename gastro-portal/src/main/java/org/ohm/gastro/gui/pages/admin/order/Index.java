package org.ohm.gastro.gui.pages.admin.order;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.gui.AbstractServiceCallback;
import org.ohm.gastro.gui.ServiceCallback;
import org.ohm.gastro.gui.pages.EditObjectPage;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Index extends EditObjectPage<OrderEntity> {

    @Property
    private OrderProductEntity oneProduct;

    @Override
    public ServiceCallback<OrderEntity> getServiceCallback() {
        return new AbstractServiceCallback<OrderEntity>() {
            @Override
            public OrderEntity findObject(final String id) {
                return getOrderService().findOrder(Long.parseLong(id));
            }
        };
    }

    public String getOrderStatus() {
        return getMessages().get(getObject().getStatus().toString());
    }

}
