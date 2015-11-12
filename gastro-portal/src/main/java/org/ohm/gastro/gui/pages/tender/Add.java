package org.ohm.gastro.gui.pages.tender;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Add extends BaseComponent {

    @Persist
    @Property
    private OrderEntity tender;

    @Property
    private OrderEntity oneSample;

    @Component(id = "tenderInfoForm")
    private Form tenderInfoForm;

    @Component(id = "name", parameters = {"value=tender.name", "validate=required"})
    private TextField name;

    @Component(id = "comment", parameters = {"value=tender.comment", "validate=required"})
    private TextArea comment;

    @Component(id = "dueDate", parameters = {"value=tender.dueDateAsString", "validate=required"})
    private TextField dueDate;

    @Component(id = "budget", parameters = {"value=tender.totalPrice"})
    private TextField budget;

//    @Component(id = "personCount", parameters = {"value=tender.personCount"})
//    private TextField personCount;

    @Inject
    @Property
    private Block samplesBlock;

    @Inject
    @Property
    private Block tenderInfoBlock;

    @Property
    private boolean error = false;

    @Property
    private boolean needLogin = false;

    public java.util.List<OrderEntity> getSamples() {
        return getOrderService().findAllTenders().stream().limit(5).collect(Collectors.toList());
    }

    public void onPrepareFromTenderInfoForm() {
        if (tender == null) tender = new OrderEntity();
    }

    public Object onSubmitFromTenderInfoForm() {
        if (tenderInfoForm.getHasErrors()) {
            error = true;
            return tenderInfoBlock;
        }
        if (!isAuthenticated()) {
            needLogin = true;
            return tenderInfoBlock;
        }
        getOrderService().placeTender(tender, getAuthenticatedUser());
        return getPageLinkSource().createPageRenderLink(AddResults.class);
    }

}
