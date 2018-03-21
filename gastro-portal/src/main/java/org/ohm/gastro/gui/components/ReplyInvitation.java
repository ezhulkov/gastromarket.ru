package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Cached;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.office.order.Index;
import org.ohm.gastro.gui.pages.office.order.List;
import org.ohm.gastro.gui.pages.office.order.Rate;

import java.util.stream.Stream;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class ReplyInvitation extends BaseComponent {

    @Cached
    public OrderEntity getOrderWithNoReply() {
        if (!isAuthenticated() ||
                getComponentResources().getPage() instanceof Index ||
                getComponentResources().getPage() instanceof Rate ||
                getComponentResources().getPage() instanceof List ||
                getRequest().getParameter("ql") != null) return null;
        final Stream<OrderEntity> closedOrders = getOrderService().findAllOrders(getAuthenticatedUser()).stream().filter(t -> t.getStatus() == Status.CLOSED);
        return closedOrders.filter(t -> !t.isClientRate()).findFirst().orElse(null);
    }

}
