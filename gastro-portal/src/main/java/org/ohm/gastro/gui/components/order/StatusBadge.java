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

    public Block getOrderActionLinkBlock() {
        if (order == null || order.getId() == null) return null;
        if (order.getStatus() == Status.CANCELLED) return orderCancelledLinkBlock;
        if (order.getStatus() == Status.CLOSED) return orderClosedLinkBlock;
        if (isAdmin() || isOrderOwner() || isOrderExecutor()) return editOrderLinkBlock;
        if (order.getCatalog() != null) return catalogAttachedLinkBlock;
        return tenderReplyLinkBlock;
    }

}
