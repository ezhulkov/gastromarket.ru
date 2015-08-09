package org.ohm.gastro.gui.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class OrdersShow extends BaseComponent {

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    @Property
    private OrderEntity.Status status;

    @Property
    private CatalogEntity catalog;

    @Property
    private OrderEntity order;

    public List<CatalogEntity> getCatalogs() {
        return getShoppingCart().getCatalogs().stream().map(Entry::getKey).collect(Collectors.toList());
    }

    public List<OrderEntity> getOrders() {
        List<OrderEntity> orders = getOrderService().findAllOrders(getAuthenticatedUser(), null);
        return orders.stream().filter(t -> t.getMetaStatus() == status).collect(Collectors.toList());
    }

    public boolean isFromCart() {
        return status == Status.NEW;
    }

}