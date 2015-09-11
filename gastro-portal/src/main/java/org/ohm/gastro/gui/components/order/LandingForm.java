package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class LandingForm extends BaseComponent {

    @InjectPage
    private org.ohm.gastro.gui.pages.tender.List page;

    @Persist
    @Property
    private OrderEntity tender;

    @Component(id = "name", parameters = {"value=tender.name"})
    private TextField name;

    @Component(id = "comment", parameters = {"value=tender.comment"})
    private TextArea comment;

    @Component(id = "dueDate", parameters = {"value=tender.dueDate"})
    private TextField dueDate;

    @Component(id = "budget", parameters = {"value=tender.totalPrice"})
    private TextField budget;

    @Component(id = "personCount", parameters = {"value=tender.personCount"})
    private TextField personCount;

    public void onPrepareFromTenderForm() {
        if (tender == null) tender = new OrderEntity();
    }

    public org.ohm.gastro.gui.pages.tender.List onSubmitFromTenderForm() {
        page.setTender(tender);
        page.setForceModal(true);
        page.setStatus(Status.NEW);
        return page;
    }

}
