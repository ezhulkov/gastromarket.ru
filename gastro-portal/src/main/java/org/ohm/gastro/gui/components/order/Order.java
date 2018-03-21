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
    @Property
    protected Block orderMainBlock;

    public java.util.List<OrderProductEntity> getItems() {
        return order == null ? getShoppingCart().getItems(catalog) : getOrderService().findAllItems(order);
    }

    public void beginRender() {
        if (order != null) {
            catalog = order.getCatalog();
        }
    }

    public String getOrderShowCatalogZoneId() {
        return String.format("orderShowCatalogZoneId%s", order == null ? catalog.getId() : order.getId());
    }

    public boolean isCanReplyTender() {
        return order != null &&
                order.getType() == OrderEntity.Type.PUBLIC &&
                order.getStatus() == Status.NEW &&
                order.getCatalog() == null;
    }

}
