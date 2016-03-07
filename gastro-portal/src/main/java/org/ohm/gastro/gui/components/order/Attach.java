package org.ohm.gastro.gui.components.order;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.pages.office.order.Index;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class Attach extends AbstractOrder {

    @Property
    @Parameter
    private CommentEntity comment;

    @Property
    private String totalPrice;

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
        totalPrice = ObjectUtils.defaultIfNull(order.getTotalPrice() == null ?
                                                       comment.getBudget() :
                                                       order.getTotalPrice(),
                                               0)
                .toString();
    }

    public Link onSuccessFromAttachTenderAjaxForm(Long uid, Long oid, Long cid) {
        if (StringUtils.isEmpty(totalPrice)) return null;
        final int tp = Integer.parseInt(totalPrice);
        if (tp <= 0) return null;
        if (attachTenderAjaxForm.getHasErrors()) return null;
        return getUserService().findUser(uid).getFirstCatalog().map(catalog -> {
            final OrderEntity tender = getOrderService().findOrder(oid);
            tender.setAttachReason(attachReason);
            tender.setTotalPrice(tp);
            getOrderService().attachTender(catalog, tender, getAuthenticatedUser());
            return getPageLinkSource().createPageRenderLinkWithContext(Index.class, true, tender.getId(), false);
        }).orElse(null);
    }

}
