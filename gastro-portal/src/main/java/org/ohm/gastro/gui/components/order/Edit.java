package org.ohm.gastro.gui.components.order;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class Edit extends BaseComponent {

    public enum Stage {
        DESC, CONTACTS
    }

    @InjectComponent
    private org.ohm.gastro.gui.components.comment.Inject inject;

    @Property
    @Parameter(defaultPrefix = BindingConstants.PROP, allowNull = true, required = false)
    private OrderEntity order;

    @Property
    @Parameter(defaultPrefix = BindingConstants.PROP, allowNull = true, required = false)
    private OrderEntity extOrder;

    @Persist
    private OrderEntity newOrder;

    @Property
    @Parameter
    private boolean tender;

    @Inject
    @Property
    private Block editDescBlock;

    @Inject
    @Property
    private Block editContactsBlock;

    @Property
    private boolean showLoginPopup = false;

    @Property
    private boolean error = false;

    @Property
    private boolean closeImmediately;

    @Property
    private boolean goBack;

    @Component(id = "fullName", parameters = {"value=order.customer.fullName", "validate=required"})
    private TextField fullName;

    @Component(id = "mobilePhone", parameters = {"value=order.customer.mobilePhone", "validate=required"})
    private TextField mobilePhone;

    @Component(id = "deliveryAddress", parameters = {"value=order.customer.deliveryAddress", "validate=required"})
    private TextField deliveryAddress;

    @Component(id = "name", parameters = {"value=order.name", "validate=required"})
    private TextField name;

    @Component(id = "comment", parameters = {"value=order.comment", "validate=required"})
    private TextArea comment;

    @Component(id = "dueDate", parameters = {"value=order.dueDateAsString", "validate=required"})
    private TextField dueDate;

    @Component(id = "budget", parameters = {"value=order.totalPrice", "validate=required"})
    private TextField budget;

    @Component(id = "personCount", parameters = {"value=order.personCount"})
    private TextField personCount;

    @Parameter(name = "ordersBlock", required = false, allowNull = false)
    private Block ordersBlock;

    @Parameter(name = "orderBlock", required = false, allowNull = false)
    private Block orderBlock;

    @Parameter(name = "orderZoneId", required = false, allowNull = false)
    private String orderZoneId;

    @Property
    @Parameter(name = "edit", defaultPrefix = BindingConstants.LITERAL, value = "true")
    private boolean editOrder;

    @Parameter(name = "reloadPage", required = false, allowNull = false, value = "false")
    private boolean reloadPage;

    @Property
    @Parameter(name = "modalId", defaultPrefix = BindingConstants.LITERAL, value = "order-new")
    private String modalId;

    public String getOrderEditZone() {
        return editOrder ? "orderEditZone" + order.getId() : "orderZoneNew";
    }

    public Long getOrderId() {
        return order == null || order.getId() == null ? null : order.getId();
    }

    public String getCloseLabel() {
        return editOrder ? getMessages().get("order.modal.save") : getMessages().get("order.modal.place");
    }

    //Desc section
    public void onPrepareFromDescAjaxForm() {
        inject.getSubmittedPhotos().clear();
        if (order == null || order.getId() == null) {
            if (extOrder != null) {
                newOrder = extOrder;
            } else if (newOrder == null) {
                newOrder = new OrderEntity();
            }
            order = newOrder;
            order.setCustomer(getAuthenticatedUserOpt().orElse(null));
        }
    }

    public void onFailureFromDescAjaxForm() {
        this.error = true;
    }

    public void onSubmitFromDescAjaxForm(Long tId) throws FileUploadException {
        if (!error) {
            final OrderEntity origOrder = tId != null ? getOrderService().findOrder(tId) : order;
            origOrder.setName(order.getName());
            origOrder.setComment(order.getComment());
            origOrder.setPersonCount(order.getPersonCount());
            origOrder.setTotalPrice(order.getTotalPrice());
            origOrder.setDueDateAsString(order.getDueDateAsString());
            if (isAuthenticated()) {
                if (origOrder.getId() == null) {
                    order = tender ?
                            getOrderService().placeTender(origOrder, getAuthenticatedUser()) :
                            getOrderService().placeOrder(origOrder);
                } else {
                    order = tender ?
                            getOrderService().saveTender(origOrder, getAuthenticatedUser()) :
                            getOrderService().saveOrder(origOrder, getAuthenticatedUser());
                }
                getPhotoService().attachPhotos(order, inject.getSubmittedPhotos());
                if (ordersBlock != null) getAjaxResponseRenderer().addRender("ordersZone", ordersBlock);
                if (orderBlock != null) getAjaxResponseRenderer().addRender(orderZoneId, orderBlock);
                getAjaxResponseRenderer().addRender(getOrderEditZone(), editContactsBlock);
            } else {
                order = newOrder;
                showLoginPopup = true;
                getAjaxResponseRenderer().addRender(getOrderEditZone(), editDescBlock);
            }
        } else {
            order = tId != null ? getOrderService().findOrder(tId) : order;
            getAjaxResponseRenderer().addRender(getOrderEditZone(), editDescBlock);
        }
    }

    //Contacts form
    public void onFailureFromContactsForm() {
        this.error = true;
    }

    public void onSelectedFromSaveAndBack() {
        goBack = true;
    }

    public void onSelectedFromSaveAndClose() {
        closeImmediately = true;
    }

    public void onPrepareFromContactsForm(Long tid) {
        order = getOrderService().findOrder(tid);
    }

    public Object onSubmitFromContactsForm(Long tid) {
        order = getOrderService().findOrder(tid);
        if (goBack) {
            getAjaxResponseRenderer().addRender(getOrderEditZone(), editDescBlock);
            return null;
        }
        if (!error) {
            getUserService().saveUser(order.getCustomer());
        } else {
            getAjaxResponseRenderer().addRender(getOrderEditZone(), editContactsBlock);
            return null;
        }
        if (closeImmediately && !editOrder) order = null;
        if (closeImmediately) getAjaxResponseRenderer().addRender(getOrderEditZone(), editDescBlock);
        return closeImmediately && reloadPage ?
                (tender ?
                        org.ohm.gastro.gui.pages.tender.Index.class :
                        org.ohm.gastro.gui.pages.office.Order.class) :
                null;
    }

}
