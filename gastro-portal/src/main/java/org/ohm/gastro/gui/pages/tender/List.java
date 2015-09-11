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

    private boolean forceModal;

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
        this.forceModal = !isCook() && forceModal;
        return true;
    }

    public OrderEntity getTender() {
        return tender;
    }

    public void setTender(OrderEntity tender) {
        this.tender = tender;
    }

    public Object onPassivate() {
        return forceModal ? new Object[]{status, true} : status != null ? status : null;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isForceModal() {
        return forceModal;
    }

    public void setForceModal(boolean forceModal) {
        this.forceModal = forceModal;
    }
}
