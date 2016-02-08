package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.OrderProductEntity;

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

//    @Inject
//    private Block clientEditBlock;

    @Inject
    private Block expiredBlock;

//    @Inject
//    private Block clientRateCook;

    @Inject
    private Block tenderReplyBlock;

//    @Inject
//    private Block cookRateClient;

    @Inject
    private Block catalogAttachedBlock;

//    @Inject
//    @Property
//    private Block editOrderBlock;

    public java.util.List<OrderProductEntity> getItems() {
        return order == null ? getShoppingCart().getItems(catalog) : getOrderService().findAllItems(order);
    }

    public void beginRender() {
        if (order != null) catalog = order.getCatalog();
    }

//    public Block onActionFromEditTender(Long tid) {
//        this.order = getOrderService().findOrder(tid);
//        return editOrderBlock;
//    }

    public String getOrderShowCatalogZoneId() {
        return String.format("orderShowCatalogZoneId%s", order == null ? catalog.getId() : order.getId());
    }

    public Status[] getStatuses() {
        return isCook() ? order.getStatus().getCookGraph() : order.getStatus().getClientGraph();
    }

    public Block getTenderAdditionalBlock() {
        if (order == null || !order.isTender()) return null;
        if (order.isTenderExpired() && order.getCatalog() == null) return expiredBlock;
        if (order.getCatalog() != null) return catalogAttachedBlock;
        return tenderReplyBlock;
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

}
