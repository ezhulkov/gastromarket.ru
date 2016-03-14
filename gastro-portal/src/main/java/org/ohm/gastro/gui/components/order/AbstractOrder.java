package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.gui.components.order.Order.Type;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.function.Supplier;

/**
 * Created by ezhulkov on 31.07.15.
 */
public abstract class AbstractOrder extends BaseComponent {

    @Parameter
    protected CatalogEntity catalog;

    @Property
    @Parameter
    protected OrderEntity order;

    @Property
    @Parameter(allowNull = false)
    protected Block orderBlock;

    @Property
    @Parameter(value = "false")
    protected boolean frontend;

    @Property
    @Parameter
    protected String orderShowCatalogZoneId;

    @Property
    @Parameter(value = "false")
    protected boolean orderPage;

    @Property
    @Parameter
    protected boolean privateOrders;

    @Property
    @Parameter
    protected java.util.List<OrderProductEntity> items;

    @Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    protected Type type = Type.SHORT;

    public boolean isBasket() {
        return type == Type.BASKET;
    }

    public boolean isEdit() {
        return type == Type.FULL;
    }

    public Integer getTotal() {
        return order.getTotalPrice();
    }

    public CatalogEntity getCatalog() {
        return catalog != null ? catalog : order != null ? order.getCatalog() : null;
    }

    public void setCatalog(final CatalogEntity catalog) {
        this.catalog = catalog;
    }

    public boolean isCanEdit() {
        return order == null || order.isCanEdit(getAuthenticatedUserSafe());
    }

    public boolean isOrderOwner() {
        return order == null || order.isOrderOwner(getAuthenticatedUserSafe()) || isAdmin();
    }

    public boolean isOrderExecutor() {
        return order != null && order.isOrderExecutor(getAuthenticatedUserSafe());
    }

    public boolean isContactsAllowed() {
        return isAdmin() || order == null || order.isContactsAllowed(getAuthenticatedUserSafe());
    }

    public boolean isCartOrOrder() {
        return order == null || order.isOrder();
    }

    public int getOrderBonus() {
        return order == null ? OrderEntity.getBonus(getTotal()) : order.getBonus();
    }

    public boolean isMainPage() {
        return type == Type.MAIN_PAGE;
    }

    public String getOrderUrl() {
        return getOrderUrlData(() -> "office/order/list",
                               () -> "tender/index",
                               () -> "office/order/index",
                               () -> "office/order/index");
    }

    public Object[] getOrderContext() {
        return getOrderUrlData(() -> new Object[]{true, Status.NEW},
                               () -> new Object[]{order.getId()},
                               () -> new Object[]{false, order.getId(), false},
                               () -> new Object[]{true, order.getId(), false});
    }

    public boolean isFull() {
        return type == Type.FULL || type == Type.MAIN_PAGE;
    }

    private <T> T getOrderUrlData(Supplier<T> cart, Supplier<T> tenderFrontend, Supplier<T> tenderBackend, Supplier<T> orderBackend) {
        if (order == null) return cart.get();
        if (frontend) return tenderFrontend.get();
        if (order.isTender()) return tenderBackend.get();
        return orderBackend.get();
    }

}
