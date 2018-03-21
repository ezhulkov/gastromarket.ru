package org.ohm.gastro.gui.components.order;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.gui.pages.office.order.Index;
import org.ohm.gastro.util.CommonsUtils;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class Attach extends AbstractOrder {

    @Property
    @Parameter
    private CommentEntity comment;

    @Property
    private String totalPrice;

    @Property
    private String deliveryPrice;

    @Component
    private Form attachTenderAjaxForm;

    @Property
    private String attachReason;

    @Component(id = "fullName", parameters = {"value=authenticatedUser.fullName", "validate=required"})
    private TextField fullName;

    @Component(id = "mobilePhone", parameters = {"value=authenticatedUser.mobilePhone", "validate=required"})
    private TextField mobilePhone;

    @Component(id = "deliveryAddress", parameters = {"value=authenticatedUser.deliveryAddress", "validate=required"})
    private TextArea deliveryAddress;

    public void onPrepareFromAttachTenderAjaxForm(Long uid, Long oid, Long cid) {
        comment = getConversationService().findComment(cid);
        order = getOrderService().findOrder(oid);
        catalog = getUserService().findUser(uid).getFirstCatalog().get();
        totalPrice = ObjectUtils.defaultIfNull(order.getTotalPrice() == null ?
                                                       comment.getBudget() :
                                                       order.getTotalPrice(),
                                               0)
                .toString();
        deliveryPrice = comment.getDeliveryBudget() == null ? "0" : comment.getDeliveryBudget().toString();
    }

    public Link onSuccessFromAttachTenderAjaxForm() {
        return CommonsUtils.parseStrToInt(totalPrice).map(tp -> {
            if (attachTenderAjaxForm.getHasErrors()) return null;
            order.setAttachReason(attachReason);
            order.setTotalPrice(tp);
            order.setDeliveryPrice(CommonsUtils.parseStrToInt(deliveryPrice).orElseGet(() -> 0));
            getOrderService().attachTender(catalog, order, getAuthenticatedUser());
            return getPageLinkSource().createPageRenderLinkWithContext(Index.class, order.getId(), true);
        }).orElseGet(() -> null);
    }

}
