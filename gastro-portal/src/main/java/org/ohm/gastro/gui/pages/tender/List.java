package org.ohm.gastro.gui.pages.tender;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class List extends BaseComponent {

    @Property
    @Inject
    private Block ordersBlock;

    private Status status;

    @Persist
    private OrderEntity tender;

    public boolean onActivate() {
        return onActivate(null);
    }

    public boolean onActivate(Status status) {
        return onActivate(status, false);
    }

    public boolean onActivate(Status status, boolean forceModal) {
        this.status = status;
        return true;
    }

    public OrderEntity getTender() {
        return tender;
    }

    public void setTender(OrderEntity tender) {
        this.tender = tender;
    }

    public Object onPassivate() {
        return status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
