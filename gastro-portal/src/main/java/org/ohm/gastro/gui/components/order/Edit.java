package org.ohm.gastro.gui.components.order;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.components.comment.InjectPhotos;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class Edit extends BaseComponent {

    @InjectComponent
    private InjectPhotos injectPhotos;

    @Property
    @Parameter(defaultPrefix = BindingConstants.PROP, allowNull = true, required = false)
    private OrderEntity order;

    @Component(id = "name", parameters = {"value=order.name", "validate=required"})
    private TextField name;

    @Component(id = "comment", parameters = {"value=order.comment"})
    private TextArea comment;

    @Component(id = "dueDate", parameters = {"value=order.dueDateAsString", "validate=required"})
    private TextField dueDate;

    @Component(id = "budget", parameters = {"value=budget", "validate=required"})
    private TextField budgetField;

    @Component(id = "promoCode", parameters = {"value=order.promoCode"})
    private TextField promoCode;

    @Property
    @Parameter(name = "modalId", defaultPrefix = BindingConstants.LITERAL, value = "order-new")
    private String modalId;

    @Property
    private String budget;

    public void onPrepareFromDescAjaxForm() {
        budget = order.getTotalPrice() == null ? "" : order.getTotalPrice().toString();
        injectPhotos.getSubmittedPhotos().clear();
    }

    public void onSubmitFromDescAjaxForm(Long tId) throws FileUploadException {
        try {
            if (order.getTotalPrice() != null) {
                final int b = Integer.parseInt(budget);
                if (b > 0) order.setTotalPrice(b);
            }
        } catch (NumberFormatException e) {
            logger.error("", e);
        }
        getOrderService().saveOrder(order);
        getPhotoService().attachPhotos(order, injectPhotos.getSubmittedPhotos());
    }

}
