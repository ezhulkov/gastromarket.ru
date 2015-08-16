package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class Filter extends BaseComponent {

    @Property
    @Parameter(allowNull = false, required = true)
    private OrderEntity.Status status;

    @Property
    @Parameter(allowNull = false, required = true)
    private boolean privateOrders;

    @Property
    @Parameter(value = "false")
    private boolean frontend;

}
