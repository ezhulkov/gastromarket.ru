package org.ohm.gastro.gui.pages.admin.orders;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class List extends BaseComponent {

    public enum Show {
        ALL_ORDERS, ACTIVE_ONLY
    }

    @Property
    private Show show = Show.ACTIVE_ONLY;

    @Property
    private OrderEntity oneOrder;

    public java.util.List<OrderEntity> getOrders() {
        return getOrderService().findAllOrders().stream()
                .filter(t -> show == Show.ALL_ORDERS || !t.isTenderExpired() && t.getMetaStatus() != Status.CLOSED)
                .sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate())).collect(Collectors.toList());
    }

    public String getStatus() {
        return getMessages().get(oneOrder.getStatus().name());
    }

    public void onActivate(Show show) {
        this.show = show;
    }

    public Object onPassivate() {
        return show;
    }

}
