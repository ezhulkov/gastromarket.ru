package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class View extends BaseComponent {

    public enum Type {
        SHORT, BASKET, EDIT
    }

    @Parameter
    @Property
    private CatalogEntity catalog;

    @Parameter(value = "false")
    @Property
    private boolean mainPage;

    @Property
    @Parameter(value = "false")
    private boolean frontend;

    @Parameter
    @Property
    private OrderEntity order;

    @Property
    @Parameter(allowNull = false, required = true)
    private boolean privateOrders;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    @Property
    private Type type = Type.SHORT;

    @Inject
    @Property
    private Block orderBlock;

    @Inject
    private Block deniedOrderBlock;

    @Inject
    private Block clientEditBlock;

    @Inject
    private Block clientPaymentBlock;

    @Inject
    private Block clientRateCook;

    @Inject
    private Block cookRateClient;

    @Inject
    @Property
    private Block editTenderBlock;

    public java.util.List<OrderProductEntity> getItems() {
        return order == null ? getShoppingCart().getItems(catalog) : getOrderService().findAllItems(order);
    }

    public void beginRender() {
        if (order != null) catalog = order.getCatalog();
    }

    public int getTotal() {
        return order == null ? getShoppingCart().getCatalogPrice(catalog) : order.getTotalPrice();
    }

    public boolean isBasket() {
        return type == Type.BASKET;
    }

    public boolean isEdit() {
        return type == Type.EDIT;
    }

    public boolean isContactsAllowed() {
        return isCook() && order.getStatus().getLevel() >= Status.PAID.getLevel();
    }

    public String getOrderShowCatalogZoneId() {
        return String.format("orderShowCatalogZoneId%s", order == null ? catalog.getId() : order.getId());
    }

    public boolean isCanEdit() {
        return !isCook() && (order == null || order.getStatus() == Status.ACTIVE || order.getStatus() == Status.NEW);
    }

    public Status[] getStatuses() {
        return isCook() ? order.getStatus().getCookGraph() : order.getStatus().getClientGraph();
    }

    public Block getEditOrderBlock() {
        if (!isCook()) {
            if (order.getStatus() == Status.CONFIRMED) return clientPaymentBlock;
            if (order.getStatus() == Status.CANCELLED || order.getStatus() == Status.CLOSED) return clientRateCook;
            if (isCanEdit()) return clientEditBlock;
        } else {
            if (order.getStatus() == Status.CANCELLED || order.getStatus() == Status.CLOSED) return cookRateClient;
        }
        return null;
    }

    public boolean isTender() {
        return order != null && order.getType() == OrderEntity.Type.PUBLIC && order.getCatalog() == null;
    }

    public Block getCurrentOrderBlock() {
        if (order != null && order.getType() == OrderEntity.Type.PUBLIC) return orderBlock;
        if (type == Type.BASKET || type == Type.SHORT) return orderBlock;
        if (isAuthenticated()) {
            return order == null || order.isAllowed(getAuthenticatedUser()) ? orderBlock : deniedOrderBlock;
        } else {
            return deniedOrderBlock;
        }
    }

    public String getEditZoneId() {
        return "editZone" + order.getId();
    }

}
