package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class List extends BaseComponent {

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    @Property
    private OrderEntity.Status status;

    @Property
    @Parameter(value = "false")
    private boolean frontend;

    @Property
    private CatalogEntity catalog;

    @Property
    private OrderEntity order;

    public java.util.List<CatalogEntity> getCatalogs() {
        return getShoppingCart().getCatalogs().stream().map(Entry::getKey).collect(Collectors.toList());
    }

    public java.util.List<OrderEntity> getOrders() {
        if (frontend) return getOrderService().findAllTenders().stream()
                .sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate()))
                .collect(Collectors.toList());
        return getOrderService().findAllOrders().stream()
                .filter(t -> t.isWasSetup() && (!frontend || !t.isTenderExpired()))
                .filter(t -> t.getCustomer().equals(getAuthenticatedUser()) || t.getCatalog() != null && t.getCatalog().getUser().equals(getAuthenticatedUser()))
                .filter(t -> t.getMetaStatus() == status || status == null)
                .sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate())).collect(Collectors.toList());
    }

}