package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OrderEntity.Status;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class Replies extends AbstractOrder {

    @Inject
    @Property
    private Block replyBlock;

    @Property
    private String replyText;

    @Property
    private CommentEntity oneComment;

    @Property
    @Parameter
    protected boolean reply;

    @Cached
    public java.util.List<CommentEntity> getComments() {
        return getRatingService().findAllComments(order);
    }

    @Cached
    public boolean isCommentAllowed() {
        if (order == null || !isAuthenticated()) return false;
        if (isCook()) {
            if (getRatingService().findAllComments(order).stream().filter(t -> t.getAuthor().equals(getAuthenticatedUser())).count() > 0) return false;
            if (getCatalogService().findAllCatalogs(getAuthenticatedUser()).stream().mapToInt(t -> getProductService().findProductsForFrontendCount(t)).sum() == 0) return false;
            return getCatalogService().findAllCatalogs(getAuthenticatedUser()).stream().
                    flatMap(t -> getOrderService().findAllOrders(t).stream()).
                    filter(t -> t.getMetaStatus() == Status.ACTIVE).
                    count() == 1;
        }
        return false;
    }

    public Block onSubmitFromReplyForm(Long oId) {
        order = getOrderService().findOrder(oId);
        getRatingService().placeReply(order, getAuthenticatedUser(), replyText);
        return replyBlock;
    }

}