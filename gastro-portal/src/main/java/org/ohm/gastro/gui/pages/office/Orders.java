package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Orders extends BaseComponent {

    @Property
    @Inject
    private Block ordersBlock;

    @Property
    private OrderEntity.Status status = Status.NEW;

    public void onActivate(OrderEntity.Status status) {
        this.status = status;
    }

    public Object[] onPassivate() {
        return new Object[]{status};
    }

}
