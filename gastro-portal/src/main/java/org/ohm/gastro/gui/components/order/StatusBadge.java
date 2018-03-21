package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.OrderEntity.Status;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class StatusBadge extends AbstractOrder {

    @Inject
    private Block catalogAttachedLinkBlock;

    @Inject
    private Block editOrderLinkBlock;

    @Inject
    private Block orderCancelledLinkBlock;

    @Inject
    private Block orderClosedLinkBlock;

    @Inject
    private Block tenderReplyLinkBlock;

    @Inject
    private Block confirmationWaitBlock;

    public Block getOrderActionLinkBlock() {
        if (order == null || order.getId() == null) return null;
        if (order.getStatus() == Status.NEW && order.isOrder()) return confirmationWaitBlock;
        if (order.getStatus() == Status.CANCELLED) return orderCancelledLinkBlock;
        if (order.getStatus() == Status.CLOSED) return orderClosedLinkBlock;
        if (order.getCatalog() != null) return catalogAttachedLinkBlock;
        if (isAdmin() || isOrderOwner() || isOrderExecutor()) return editOrderLinkBlock;
        return tenderReplyLinkBlock;
    }

    public boolean isShowClosed() {
        return !orderPage || !order.getCustomer().equals(getAuthenticatedUserSafe()) || order.isOrderClosedAndRated();
    }

    public Object[] getContext() {
        return new Object[]{order.getCustomer().getId(), order.getCatalog().getUser().getId()};
    }

}
