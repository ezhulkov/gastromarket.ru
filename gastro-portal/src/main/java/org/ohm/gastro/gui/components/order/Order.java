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

    @Inject
    private Block deniedOrderBlock;

    @Inject
    private Block clientEditBlock;

    @Inject
    private Block clientRateCook;

    @Inject
    private Block tenderReplyBlock;

    @Inject
    private Block cookRateClient;

    @Inject
    private Block catalogAttachedBlock;

    @Inject
    @Property
    private Block editOrderBlock;

    @Inject
    @Property
    protected Block orderMainBlock;

    public java.util.List<OrderProductEntity> getItems() {
        return order == null ? getShoppingCart().getItems(catalog) : getOrderService().findAllItems(order);
    }

    public void beginRender() {
        if (order != null) catalog = order.getCatalog();
    }

    public Block onActionFromEditTender(Long tid) {
        this.order = getOrderService().findOrder(tid);
        return editOrderBlock;
    }

    public boolean isFull() {
        return type == Type.FULL || type == Type.MAIN_PAGE;
    }

    public boolean isMainPage() {
        return type == Type.MAIN_PAGE;
    }

    public String getOrderShowCatalogZoneId() {
        return String.format("orderShowCatalogZoneId%s", order == null ? catalog.getId() : order.getId());
    }

    public Status[] getStatuses() {
        return isCook() ? order.getStatus().getCookGraph() : order.getStatus().getClientGraph();
    }

    public Block getOrderAdditionalBlock() {
        if (isCanReplyTender()) return tenderReplyBlock;
        if (!isCook()) {
            if (!frontend && order != null && (order.getStatus() == Status.CANCELLED || order.getStatus() == Status.CLOSED)) return clientRateCook;
            if (isCanEdit()) return clientEditBlock;
        } else {
            if (order != null && (order.getStatus() == Status.CANCELLED || order.getStatus() == Status.CLOSED)) return cookRateClient;
        }
        return order.getCatalog() != null ? catalogAttachedBlock : null;
    }

    public boolean isCanReplyTender() {
        return order != null &&
                order.getType() == OrderEntity.Type.PUBLIC &&
                order.getStatus() == Status.NEW &&
                !order.getCustomer().equals(getAuthenticatedUserOpt().orElse(null));
    }

    public Block getCurrentOrderBlock() {
        if (order != null && order.getType() == OrderEntity.Type.PUBLIC) return orderMainBlock;
        if (type == Type.BASKET || type == Type.SHORT || type == Type.MAIN_PAGE) return orderMainBlock;
        if (isAuthenticated()) {
            return order == null || order.isAllowed(getAuthenticatedUser()) ? orderMainBlock : deniedOrderBlock;
        } else {
            return deniedOrderBlock;
        }
    }

    public String getEditZoneId() {
        return order == null ? "editZoneNew" : "editZone" + order.getId();
    }

    public boolean isCanEditOrder() {
        return isAuthenticated() && order != null && order.getCustomer().equals(getAuthenticatedUser());
    }

}
