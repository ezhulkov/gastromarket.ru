package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.gui.components.order.View.Type;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.07.15.
 */
public abstract class AbstractOrder extends BaseComponent {

    @Property
    @Parameter
    protected CatalogEntity catalog;

    @Property
    @Parameter
    protected OrderEntity order;

    @Property
    @Parameter
    protected Block orderBlock;

    @Property
    @Parameter
    protected String orderShowCatalogZoneId;

    @Property
    @Parameter
    protected boolean privateOrders;

    @Property
    @Parameter
    protected java.util.List<OrderProductEntity> items;

    @Property
    @Parameter
    protected Type type;

    public boolean isTender() {
        return order != null && order.getType() == OrderEntity.Type.PUBLIC && order.getCatalog() == null;
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

    public boolean isCanEdit() {
        return !isCook() && (order == null || order.getStatus() == Status.ACTIVE || order.getStatus() == Status.NEW);
    }

    public int getTotal() {
        return order == null ? getShoppingCart().getCatalogPrice(catalog) : order.getTotalPrice();
    }

}
