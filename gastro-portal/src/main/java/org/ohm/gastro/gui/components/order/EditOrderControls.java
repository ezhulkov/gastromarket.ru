package org.ohm.gastro.gui.components.order;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.util.CommonsUtils;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class EditOrderControls extends AbstractOrder {

    @Inject
    private Block editOrderBlock;

    @Property
    private String totalPrice;

    @Property
    private String deliveryPrice;

    public void onPrepareFromCancelTenderAjaxForm(Long id) {
        this.order = getOrderService().findOrder(id);
    }

    public void onSubmitFromCancelTenderAjaxForm() {
        getOrderService().cancelOrder(order);
    }

    public void onPrepareFromConfirmOrderAjaxForm(Long id) {
        this.order = getOrderService().findOrder(id);
        totalPrice = order.getTotalPrice() == null ? "" : order.getTotalPrice().toString();
        deliveryPrice = order.getDeliveryPrice() == null ? "" : order.getDeliveryPrice().toString();
    }

    public void onSubmitFromConfirmOrderAjaxForm() {
        if (StringUtils.isEmpty(totalPrice)) {
            return;
        }
        CommonsUtils.parseStrToInt(totalPrice)
                .ifPresent(tp -> {
                    order.setTotalPrice(tp);
                    order.setDeliveryPrice(CommonsUtils.parseStrToInt(deliveryPrice).orElse(null));
                    getOrderService().confirmOrder(order);
                });

    }

    public Block onActionFromEditOrder(Long tid) {
        this.order = getOrderService().findOrder(tid);
        return editOrderBlock;
    }

    public String getEditZoneId() {
        return order == null ? "editZoneNew" : "editZone" + order.getId();
    }

    public boolean isNeedRate() {
        return order.getCustomer().equals(getAuthenticatedUserSafe()) && order.getStatus() == Status.CLOSED && !order.isClientRate();
    }

    public boolean isShowControls() {
        if (order.getStatus() == Status.CANCELLED || order.getId() == null) return false;
        if (order.isOrderClosedAndRated()) return false;
        if (!isAdmin() && !isOrderOwner() && !isOrderExecutor()) return false;
        return true;
    }

    public Object[] getContext() {
        return new Object[]{order.getCustomer().getId(), order.getCatalog().getUser().getId()};
    }

    public boolean isWaitApprove() {
        return isAdmin() && !order.isAnnonceSent();
    }

}
