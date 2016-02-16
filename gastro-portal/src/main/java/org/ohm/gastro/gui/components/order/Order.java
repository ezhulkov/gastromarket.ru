package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.gui.pages.office.ClientRate;
import org.ohm.gastro.gui.pages.office.CookRate;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class Order extends AbstractOrder {

    public enum Type {
        SHORT, BASKET, FULL, MAIN_PAGE
    }

    @Parameter(value = "false")
    @Property
    private boolean reloadPage;

    @Parameter(value = "false")
    @Property
    private boolean replies;

    @Property
    private String cancelReason;

//    @Inject
//    private Block clientRateCook;

    @Inject
    private Block tenderReplyLinkBlock;

    @Inject
    private Block editOrderBlock;

    @Inject
    private Block editBlock;

//    @Inject
//    private Block cookRateClient;

    @Inject
    @Property
    protected Block orderMainBlock;

    @Inject
    private Block catalogAttachedLinkBlock;

    @Inject
    private Block editOrderLinkBlock;

    @Inject
    private Block orderCancelledLinkBlock;

    @Inject
    private Block orderClosedLinkBlock;

    public java.util.List<OrderProductEntity> getItems() {
        return order == null ? getShoppingCart().getItems(catalog) : getOrderService().findAllItems(order);
    }

    public void beginRender() {
        if (order != null) catalog = order.getCatalog();
    }

    public String getOrderShowCatalogZoneId() {
        return String.format("orderShowCatalogZoneId%s", order == null ? catalog.getId() : order.getId());
    }

    public Block onActionFromEditOrder(Long tid) {
        this.order = getOrderService().findOrder(tid);
        return editOrderBlock;
    }

    public Status[] getStatuses() {
        return isCook() ? order.getStatus().getCookGraph() : order.getStatus().getClientGraph();
    }

    public Block getOrderActionLinkBlock() {
        if (order == null || order.getId() == null) return null;
        if (order.getStatus() == Status.CANCELLED) return orderCancelledLinkBlock;
        if (order.getStatus() == Status.CLOSED) return orderClosedLinkBlock;
        if (isOrderOwner() || isOrderExecutor()) return editOrderLinkBlock;
        if (order.getCatalog() != null) {
            return catalogAttachedLinkBlock;
        }
        return tenderReplyLinkBlock;
    }

    public Block getOrderEditBlock() {
        if (order.getStatus() == Status.CANCELLED || order.getId() == null) return null;
        if (!isOrderOwner() && !isOrderExecutor()) return null;
        return editBlock;
    }

    public boolean isCanReplyTender() {
        return order != null &&
                order.getType() == OrderEntity.Type.PUBLIC &&
                order.getStatus() == Status.NEW &&
                order.getCatalog() == null;
    }

    public String getEditZoneId() {
        return order == null ? "editZoneNew" : "editZone" + order.getId();
    }

    public void onPrepareFromCancelTenderAjaxForm(Long id) {
        this.order = getOrderService().findOrder(id);
    }

    public void onSubmitFromCancelTenderAjaxForm() {
        getOrderService().cancelOrder(order);
    }

    public String getRatePage() {
        if (order.getCustomer().equals(getAuthenticatedUser())) return getPageLinkSource().createPageRenderLinkWithContext(CookRate.class, order.getId()).getBasePath();
        if (order.getCatalog().getUser().equals(getAuthenticatedUser())) return getPageLinkSource().createPageRenderLinkWithContext(ClientRate.class, order.getId()).getBasePath();
        return null;
    }

}
