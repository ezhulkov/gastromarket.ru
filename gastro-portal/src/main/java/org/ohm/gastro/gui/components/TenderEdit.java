package org.ohm.gastro.gui.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class TenderEdit extends BaseComponent {

    public enum Stage {
        DESC, CONTACTS
    }

    @Property
    @Parameter(defaultPrefix = BindingConstants.PROP, allowNull = true, required = false)
    private OrderEntity tender;

    @Inject
    @Property
    private Block editDescBlock;

    @Inject
    @Property
    private Block editContactsBlock;

    @Property
    private boolean error = false;

    @Property
    private boolean closeImmediately;

    @Property
    private boolean goBack;

    @Component(id = "fullName", parameters = {"value=tender.customer.fullName", "validate=required"})
    private TextField fullName;

    @Component(id = "mobilePhone", parameters = {"value=tender.customer.mobilePhone", "validate=required"})
    private TextField mobilePhone;

    @Component(id = "deliveryAddress", parameters = {"value=tender.customer.deliveryAddress", "validate=required"})
    private TextField deliveryAddress;

    @Component(id = "comment", parameters = {"value=tender.comment", "validate=required"})
    private TextArea comment;

    @Component(id = "dueDate", parameters = {"value=tender.dueDate"})
    private TextField dueDate;

    @Component(id = "budget", parameters = {"value=tender.totalPrice", "validate=required"})
    private TextField budget;

    @Component(id = "personCount", parameters = {"value=tender.personCount"})
    private TextField personCount;

    @Parameter(name = "tendersBlock", required = false, allowNull = false)
    private Block tendersBlock;

    @Parameter(name = "tenderBlock", required = false, allowNull = false)
    private Block tenderBlock;

    @Parameter(name = "tenderZoneId", required = false, allowNull = false)
    private String tenderZoneId;

    @Property
    @Parameter(name = "edit", defaultPrefix = BindingConstants.LITERAL, value = "true")
    private boolean editTender;

    @Parameter(name = "reloadPage", required = false, allowNull = false, value = "false")
    private boolean reloadPage;

    @Property
    @Parameter(name = "modalId", defaultPrefix = BindingConstants.LITERAL, value = "tender-new")
    private String modalId;

    public String getTenderEditZone() {
        return editTender ? "tenderEditZone" + tender.getId() : "tenderZoneNew";
    }

    public Long getTenderId() {
        return tender == null || tender.getId() == null ? null : tender.getId();
    }

    public String getCloseLabel() {
        return editTender ? getMessages().get("tender.modal.save") : getMessages().get("tender.modal.place");
    }

    //Desc section
    public void onPrepareFromDescForm() {
        if (tender == null || tender.getId() == null) {
            tender = new OrderEntity();
            tender.setCustomer(getAuthenticatedUserOpt().orElse(null));
            tender.setType(OrderEntity.Type.PRIVATE);
        }
    }

    public void onFailureFromDescForm() {
        this.error = true;
    }

    public void onSubmitFromDescForm(Long tId) {
        if (!error) {
            final OrderEntity origOrder = tId != null ? getOrderService().findOrder(tId) : tender;
            origOrder.setComment(tender.getComment());
            origOrder.setPersonCount(tender.getPersonCount());
            origOrder.setTotalPrice(tender.getTotalPrice());
            origOrder.setDueDate(tender.getDueDate());
            if (origOrder.getId() == null) {
                tender = getOrderService().placeTender(origOrder, getAuthenticatedUser());
            } else {
                tender = getOrderService().saveTender(origOrder, getAuthenticatedUser());
            }
            if (tendersBlock != null) getAjaxResponseRenderer().addRender("tendersZone", tendersBlock);
            if (tenderBlock != null) getAjaxResponseRenderer().addRender(tenderZoneId, tenderBlock);
            getAjaxResponseRenderer().addRender(getTenderEditZone(), editContactsBlock);
        } else {
            getAjaxResponseRenderer().addRender(getTenderEditZone(), editDescBlock);
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
        tender = getOrderService().findOrder(tid);
    }

    public Object onSubmitFromContactsForm(Long tid) {
        tender = getOrderService().findOrder(tid);
        if (goBack) {
            getAjaxResponseRenderer().addRender(getTenderEditZone(), editDescBlock);
            return null;
        }
        if (!error) {
            getUserService().saveUser(tender.getCustomer());
        } else {
            getAjaxResponseRenderer().addRender(getTenderEditZone(), editContactsBlock);
        }
        if (closeImmediately && !editTender) tender = null;
        if (closeImmediately) getAjaxResponseRenderer().addRender(getTenderEditZone(), editDescBlock);
        return closeImmediately && reloadPage ? org.ohm.gastro.gui.pages.tender.Index.class : null;
    }

}
