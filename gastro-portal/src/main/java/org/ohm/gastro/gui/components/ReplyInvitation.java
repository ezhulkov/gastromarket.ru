package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Cached;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.office.CookRate;
import org.ohm.gastro.gui.pages.office.Order;
import org.ohm.gastro.gui.pages.office.Orders;

import java.util.stream.Stream;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class ReplyInvitation extends BaseComponent {

    @Cached
    public OrderEntity getOrderWithNoReply() {
        if (!isAuthenticated() ||
                getComponentResources().getPage() instanceof Order ||
                getComponentResources().getPage() instanceof CookRate ||
                getComponentResources().getPage() instanceof Orders ||
                getRequest().getParameter("ql") != null) return null;
        final Stream<OrderEntity> closedOrders = getOrderService().findAllOrders(getAuthenticatedUser(), null).stream().filter(t -> t.getStatus() == Status.CLOSED);
        return closedOrders.filter(t -> !t.isClientRate()).findFirst().orElse(null);
    }

}
