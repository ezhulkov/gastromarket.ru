package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
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

    public boolean isBasket() {
        return type == Type.BASKET;
    }

    public boolean isEdit() {
        return type == Type.FULL;
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

    public boolean isCanEdit() {
        return order == null || order.isCanEdit(getAuthenticatedUserSafe());
    }

    public boolean isOrderOwner() {
        return order == null || order.isOrderOwner(getAuthenticatedUserSafe());
    }

    public boolean isContactsAllowed() {
        return order == null || order.isContactsAllowed(getAuthenticatedUserSafe());
    }

    public boolean isCartOrOrder() {
        return order == null || order.isOrder();
    }

    public int getOrderBonus() {
        return order == null ? OrderEntity.getBonus(getTotal()) : order.getBonus();
    }

}
