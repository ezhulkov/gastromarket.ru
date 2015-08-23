package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.ohm.gastro.domain.OrderEntity.Status;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class Edit extends AbstractOrder {

    @Parameter
    private Block editTenderBlock;

    @Component(id = "comment", parameters = {"value=order.comment", "validate=maxlength=1024"})
    private TextArea comment2Field;

    @Component(id = "deliveryAddress", parameters = {"value=order.customer.deliveryAddress", "validate=required"})
    private TextArea dAddress2Field;

    @Component(id = "mobilePhone", parameters = {"value=order.customer.mobilePhone", "validate=required"})
    private TextField mobile2Field;

    @Component(id = "fullName", parameters = {"value=order.customer.fullName", "validate=required"})
    private TextField fName2Field;

    @Component(id = "dueDate", parameters = {"value=order.dueDate"})
    private TextField dueDate2Field;

    @Component(id = "promoCode", parameters = {"value=order.promoCode"})
    private TextField promoCode2Field;

    public void onPrepareFromOrderDetailsForm(Long oId) {
        this.order = getOrderService().findOrder(oId);
        this.catalog = order.getCatalog();
    }

    public Block onSuccessFromOrderDetailsForm(Long oId) {
        getOrderService().saveOrder(order, getAuthenticatedUser());
        return orderBlock;
    }

    public boolean isCanEditTender() {
        return isAuthenticated() && order != null && order.getCustomer() != null && order.getCustomer().equals(getAuthenticatedUser()) && order.getStatus() == Status.NEW;
    }

    public String getEditZoneId() {
        return "editZone" + order.getId();
    }

    public Block onActionFromEditTender(Long tid) {
        this.order = getOrderService().findOrder(tid);
        return editTenderBlock;
    }

}
