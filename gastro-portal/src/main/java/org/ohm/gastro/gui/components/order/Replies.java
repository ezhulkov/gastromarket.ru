package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CommentEntity;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class Replies extends AbstractOrder {

    @Property
    private CommentEntity oneComment;

    @Cached
    public java.util.List<CommentEntity> getComments() {
        return getRatingService().findAllComments(order);
    }

    @Cached
    public boolean isCommentAllowed() {
        return order != null && isAuthenticated() && (order.getCustomer().equals(getAuthenticatedUser()) || isCook());
    }

}