package org.ohm.gastro.gui.pages.tender;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.components.comment.InjectPhotos;
import org.ohm.gastro.gui.pages.AbstractPage;

import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Add extends AbstractPage {

    @Persist
    @Property
    private OrderEntity tender;

    @Property
    private OrderEntity oneSample;

    @Property
    @Persist
    private String mobile;

    @Component(id = "tenderInfoForm")
    private Form tenderInfoForm;

    @Component(id = "name", parameters = {"value=tender.name", "validate=required"})
    private TextField name;

    @Component(id = "comment", parameters = {"value=tender.comment", "validate=required"})
    private TextArea comment;

    @Component(id = "mobile", parameters = {"value=mobile", "validate=required"})
    private TextField mobileField;

    @Component(id = "dueDate", parameters = {"value=tender.dueDateAsString", "validate=required"})
    private TextField dueDate;

    @Component(id = "promoCode", parameters = {"value=tender.promoCode"})
    private TextField promoCode;

    @Component(id = "budget", parameters = {"value=budget", "validate=regexp"})
    private TextField budgetField;

    @Property
    private String budget;

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

    @InjectComponent
    private InjectPhotos injectPhotos;

    @Persist
    @Property
    private java.util.List<PhotoEntity> photos;

    public java.util.List<OrderEntity> getSamples() {
        return getOrderService().findAllTenders().stream().limit(5).collect(Collectors.toList());
    }

    public void onPrepareFromTenderInfoForm() {
        if (tender == null || tender.getId() != null) tender = new OrderEntity();
        if (mobile == null) mobile = getAuthenticatedUserOpt().map(UserEntity::getMobilePhone).orElse(null);
    }

    public Object onSubmitFromTenderInfoForm() {
        photos = injectPhotos.getSubmittedPhotos();
        if (tenderInfoForm.getHasErrors() || mobile == null || tender.getDueDate() == null) {
            error = true;
            return tenderInfoBlock;
        }
        if (!isAuthenticated()) {
            needLogin = true;
            return tenderInfoBlock;
        }
        if (!mobile.equals(getAuthenticatedUser().getMobilePhone())) {
            getAuthenticatedUser().setMobilePhone(mobile);
            getUserService().saveUser(getAuthenticatedUser());
        }
        tender.setTotalPrice(null);
        if (StringUtils.isNotEmpty(budget)) {
            budget = budget.replaceAll("\\.", "").replaceAll(",", "");
            if (StringUtils.isNotEmpty(budget)) {
                final int i = Integer.parseInt(budget);
                if (i > 0) tender.setTotalPrice(i);
            }
        }
        tender.setCustomer(getAuthenticatedUser());
        final OrderEntity newTender = getOrderService().saveOrder(tender);
        getPhotoService().attachPhotos(newTender, photos);
        getOrderService().placeTender(newTender, getAuthenticatedUser());
        photos.clear();
        injectPhotos.purgeSubmittedPhotos();
        return getPageLinkSource().createPageRenderLinkWithContext(AddResults.class, newTender.getId());
    }

}
