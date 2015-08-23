package org.ohm.gastro.gui.pages.tender;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class List extends BaseComponent {

    @Property
    @Inject
    private Block ordersBlock;

    @Property
    private Status status = Status.NEW;

    public boolean onActivate() {
        return onActivate(Status.NEW);
    }

    public boolean onActivate(Status status) {
        this.status = status;
        return true;
    }

    public Object[] onPassivate() {
        return new Object[]{status};
    }

}
