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
    @Parameter
    protected String orderShowCatalogZoneId;

    @Property
    @Parameter(value = "false")
    protected boolean frontend;

    @Property
    @Parameter
    protected boolean mainPage;

    @Property
    @Parameter
    protected boolean privateOrders;

    @Property
    @Parameter
    protected java.util.List<OrderProductEntity> items;

    @Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    protected Type type = Type.SHORT;

    public boolean isTender() {
        return isTenderType() && order.getCatalog() == null;
    }

    public boolean isTenderType() {
        return order != null && order.getType() == OrderEntity.Type.PUBLIC;
    }

    public boolean isBasket() {
        return type == Type.BASKET;
    }

    public boolean isEdit() {
        return type == Type.FULL;
    }

    public boolean isContactsAllowed() {
        return isCook() && order.getStatus().getLevel() >= Status.CONFIRMED.getLevel();
    }

    public boolean isOrderClosed() {
        return order != null && order.getStatus().getLevel() >= Status.CLOSED.getLevel();
    }

    public boolean isCanEdit() {
        return !isCook() && (order == null || order.getStatus() == Status.ACTIVE || order.getStatus() == Status.NEW);
    }

    public boolean isOrderCustomer() {
        return order != null && order.getCustomer().equals(getAuthenticatedUserOpt().orElse(null));
    }

    public int getTotal() {
        return order == null ? getShoppingCart().getCatalogPrice(catalog) : order.getTotalPrice();
    }

    public CatalogEntity getCatalog() {
        return catalog != null ? catalog : order != null ? order.getCatalog() : null;
    }

    public void setCatalog(final CatalogEntity catalog) {
        this.catalog = catalog;
    }
}
