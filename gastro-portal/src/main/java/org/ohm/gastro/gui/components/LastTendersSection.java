package org.ohm.gastro.gui.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 30.05.15.
 */
public class LastTendersSection extends BaseComponent {

    @Property
    private OrderEntity oneTender;

    @Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String additionalClass;

    @Cached
    public List<OrderEntity> getTenders() {
        return getOrderService().findAllTenders().stream()
                .filter(t -> !(t.isTenderExpired() && t.getCatalog() == null))
                .filter(OrderEntity::isWasSetup)
                .sorted(((o1, o2) -> o2.getDate().compareTo(o1.getDate())))
                .limit(3).collect(Collectors.toList());
    }

}
