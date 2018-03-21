package org.ohm.gastro.gui.components.comment;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.util.CommonsUtils;

/**
 * Created by ezhulkov on 13.08.15.
 */
public class Reply extends BaseComponent {

    @Property
    @Parameter(allowNull = true, required = false)
    private CommentEntity comment;

    @Property
    @Parameter(allowNull = false, required = false)
    private OrderEntity order;

    @Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String btnTitle;

    @InjectComponent
    private InjectPhotos injectPhotos;

    @Property
    private String budget;

    @Property
    private String deliveryBudget;

    @Property
    private String replyTime;

    public void onPrepareFromReplyAjaxForm(Long cid, Long oid) {
        injectPhotos.purgeSubmittedPhotos();
        if (oid != null) {
            order = getOrderService().findOrder(oid);
            comment = new CommentEntity();
            comment.setEntity(order);
            comment.setAuthor(getAuthenticatedUser());
        } else if (cid != null) {
            comment = getConversationService().findComment(cid);
            order = (OrderEntity) comment.getEntity();
            budget = comment.getBudget() == null ? "" : comment.getBudget().toString();
            deliveryBudget = comment.getDeliveryBudget() == null ? "" : comment.getDeliveryBudget().toString();
            replyTime = comment.getReplyExpirationHours() == null ? "" : comment.getReplyExpirationHours().toString();
        }
    }

    public void onSuccessFromReplyAjaxForm(Long cid, Long oId) {
        final java.util.List<CommentEntity> replies = getConversationService().findAllComments(order);
        if (replies.stream().anyMatch(t -> t.getAuthor().equals(getAuthenticatedUserSafe()))) return;
        comment.setBudget(CommonsUtils.parseStrToInt(budget).orElseGet(() -> comment.getBudget()));
        comment.setDeliveryBudget(CommonsUtils.parseStrToInt(deliveryBudget).orElseGet(() -> null));
        comment.setReplyExpirationHours(CommonsUtils.parseStrToInt(replyTime).orElseGet(() -> null));
        final boolean newComment = comment.getId() == null;
        getConversationService().saveComment(comment);
        final java.util.List<PhotoEntity> photos = injectPhotos.getSubmittedPhotos();
        injectPhotos.purgeSubmittedPhotos();
        getPhotoService().attachPhotos(comment, photos);
        if (newComment) {
            getConversationService().placeTenderReply(order, comment, getAuthenticatedUser());
        }
    }

    public Object[] getFormContext() {
        return new Object[]{comment == null ? null : comment.getId(), order == null ? null : order.getId()};
    }

    public java.util.List<ProductEntity> getProducts() {
        return getAuthenticatedUser().getFirstCatalog()
                .map(c -> getProductService().findProductsForFrontend(null, c, true, false, null, null, null, null, 0, Integer.MAX_VALUE))
                .orElse(null);
    }

}
