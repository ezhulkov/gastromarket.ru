package org.ohm.gastro.gui.components.comment;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

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
    private String replyTime;

    public void onPrepareFromForm(Long cid, Long oid) {
        injectPhotos.getSubmittedPhotos().clear();
        if (oid != null) {
            order = getOrderService().findOrder(oid);
            comment = new CommentEntity();
            comment.setEntity(order);
            comment.setAuthor(getAuthenticatedUser());
        } else if (cid != null) {
            comment = getConversationService().findComment(cid);
            order = (OrderEntity) comment.getEntity();
            budget = comment.getBudget() == null ? "" : comment.getBudget().toString();
            replyTime = comment.getReplyExpirationHours() == null ? "" : comment.getReplyExpirationHours().toString();
        }
    }

    public void onSuccessFromForm(Long cid, Long oId) {
        try {
            if (StringUtils.isNotEmpty(budget)) {
                final int b = Integer.parseInt(budget);
                if (b > 0) comment.setBudget(b);
            }
            if (StringUtils.isNotEmpty(replyTime)) {
                final int o = Integer.parseInt(replyTime);
                if (o > 0) comment.setReplyExpirationHours(o);
            } else {
                comment.setReplyExpirationHours(null);
            }
        } catch (NumberFormatException e) {
            logger.error("", e);
        }

        final boolean newComment = comment.getId() == null;
        getConversationService().saveComment(comment);
        getPhotoService().attachPhotos(comment, injectPhotos.getSubmittedPhotos());
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
