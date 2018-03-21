package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.filter.RegionFilter;
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
        final UserEntity user = getAuthenticatedUserSafe();
        final java.util.List<OrderEntity> orders = frontend ?
                getOrderService().findAllTenders(RegionFilter.getCurrentRegion()) :
                getOrderService().findAllOrders().stream()
                        .filter(t -> t.getCustomer().equals(user) || t.getCatalog() != null && t.getCatalog().getUser().equals(user))
                        .collect(Collectors.toList());
        return orders.stream()
                .filter(t -> t.isWasSetup() || t.getCustomer().equals(user))
                .filter(t -> !frontend || !t.isTenderExpired())
                .filter(t -> t.getMetaStatus() == status || status == null)
                .sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate())).collect(Collectors.toList());
    }

}