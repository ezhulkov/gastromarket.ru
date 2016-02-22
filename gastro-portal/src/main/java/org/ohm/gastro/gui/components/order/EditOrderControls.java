package org.ohm.gastro.gui.components.order;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.OrderEntity.Status;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class EditOrderControls extends AbstractOrder {

    @Property
    private String cancelReason;

    @Inject
    private Block editBlock;

    @Property
    private String totalPrice;

    @Property
    private String survey;

    @Inject
    private Block editOrderBlock;

    public void beginRender() {
        if (order != null) {
            totalPrice = order.getTotalPrice() == null ? "" : order.getTotalPrice().toString();
        }
    }

    public Block getOrderEditBlock() {
        if (order.getStatus() == Status.CANCELLED || order.getId() == null) return null;
        if (!isAdmin() && !isOrderOwner() && !isOrderExecutor()) return null;
        return editBlock;
    }

    public void onPrepareFromCancelTenderAjaxForm(Long id) {
        this.order = getOrderService().findOrder(id);
    }

    public void onSubmitFromCancelTenderAjaxForm() {
        getOrderService().cancelOrder(order);
    }

    public void onPrepareFromCloseTenderAjaxForm(Long id) {
        this.order = getOrderService().findOrder(id);
    }

    public void onSubmitFromCloseTenderAjaxForm() {
        if (StringUtils.isNotEmpty(totalPrice)) {
            final int tp = Integer.parseInt(totalPrice);
            if (tp > 0) {
                getOrderService().closeOrder(order, tp, survey, getAuthenticatedUser());
            }
        }
    }

    public Block onActionFromEditOrder(Long tid) {
        this.order = getOrderService().findOrder(tid);
        return editOrderBlock;
    }

    public String getEditZoneId() {
        return order == null ? "editZoneNew" : "editZone" + order.getId();
    }

}
