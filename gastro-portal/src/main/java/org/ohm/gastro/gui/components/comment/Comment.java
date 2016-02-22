package org.ohm.gastro.gui.components.comment;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.CommentableEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.office.Order;

/**
 * Created by ezhulkov on 14.02.15.
 */
public class Comment extends BaseComponent {

    @Property
    @Parameter
    private CommentEntity comment;

    @Property
    @Parameter
    private CommentableEntity entity;

    @Property
    @Parameter(value = "false")
    private boolean reply;

    @Property
    private String attachReason;

    @Property
    private String totalPrice;

    @Parameter(defaultPrefix = BindingConstants.LITERAL, value = "true")
    private boolean showEdit;

    @Component(id = "fullName", parameters = {"value=authenticatedUser.fullName", "validate=required"})
    private TextField fullName;

    @Component(id = "mobilePhone", parameters = {"value=authenticatedUser.mobilePhone", "validate=required"})
    private TextField mobilePhone;

    @Component(id = "deliveryAddress", parameters = {"value=authenticatedUser.deliveryAddress", "validate=required"})
    private TextArea deliveryAddress;

    @Component
    private Form attachTenderAjaxForm;

    public boolean isCookReply() {
        return comment.getAuthor().isCook();
    }

    public boolean isCanEditComment() {
        return showEdit && getAuthenticatedUserOpt().map(t -> t.isAdmin() || t.equals(comment.getAuthor())).orElse(false);
    }

    public boolean isOrderOwner() {
        return getOrder().isOrderOwner(getAuthenticatedUserSafe());
    }

    public void onPrepareFromAttachTenderAjaxForm(Long uid, Long oid, Long cid) {
        if (getOrder() != null) {
            comment = getConversationService().findComment(cid);
            totalPrice = ObjectUtils.defaultIfNull(getOrder().getTotalPrice() == null ?
                                                           comment.getBudget() :
                                                           getOrder().getTotalPrice(),
                                                   0)
                    .toString();
        }
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
            return getPageLinkSource().createPageRenderLinkWithContext(Order.class, true, tender.getId(), false);
        }).orElse(null);
    }

    public OrderEntity getOrder() {
        return entity instanceof OrderEntity ? (OrderEntity) entity : null;
    }

    public boolean isAttachedCatalog() {
        return getOrder().isOrderAttached() && getOrder().getCatalog().equals(comment.getAuthor().getFirstCatalog().orElse(null));
    }

}
