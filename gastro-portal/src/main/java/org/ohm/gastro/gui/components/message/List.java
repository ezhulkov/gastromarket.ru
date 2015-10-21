package org.ohm.gastro.gui.components.message;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.Date;

/**
 * Created by ezhulkov on 21.10.15.
 */
public class List extends BaseComponent {

    @Property
    private CommentEntity comment;

    @Property
    @Parameter
    private java.util.List<CommentEntity> comments;

    @Property
    @Parameter
    private Date lastSeen;

    @Property
    @Parameter(value = "true", defaultPrefix = BindingConstants.LITERAL)
    private boolean showEmpty;

    public String getUnread() {
        return lastSeen != null && !comment.getAuthor().equals(getAuthenticatedUser()) && comment.getDate().after(lastSeen) ? "unread" : "";
    }

    public Object onActionFromDeleteComment(Long cId) {
        getConversationService().deleteComment(cId);
        return null;
    }

    public boolean isCommentOwner() {
        return comment.getAuthor().equals(getAuthenticatedUser());
    }

}
