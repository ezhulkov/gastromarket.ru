package org.ohm.gastro.gui.components.comment;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.CommentableEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

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

    @Parameter(defaultPrefix = BindingConstants.LITERAL, value = "true")
    private boolean showEdit;

    public boolean isCookReply() {
        return comment.getAuthor().isCook();
    }

    public boolean isCanEditComment() {
        return showEdit && getAuthenticatedUserOpt().map(t -> t.isAdmin() || t.equals(comment.getAuthor())).orElse(false);
    }

    public boolean isOrderOwner() {
        return getOrder().isOrderOwner(getAuthenticatedUserSafe());
    }

    public OrderEntity getOrder() {
        return entity instanceof OrderEntity ? (OrderEntity) entity : null;
    }

    public boolean isAttachedCatalog() {
        return getOrder().isOrderAttached() && getOrder().getCatalog().equals(comment.getAuthor().getFirstCatalog().orElse(null));
    }

}
