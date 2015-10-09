package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Message extends BaseComponent {

    @Property
    private ConversationEntity conversation;

    @Property
    private CommentEntity comment;

    @Property
    @Inject
    private Block messageBlock;

    private Date lastSeen;

    public void onActivate(Long id) {
        conversation = getConversationService().find(id);
        lastSeen = conversation.getLastSeenDate();
        conversation.setLastSeenDate(new Date());
        getConversationService().save(conversation);
    }

    public Long onPassivate() {
        return conversation == null ? null : conversation.getId();
    }

    public List<CommentEntity> getComments() {
        return getRatingService().findAllComments(conversation).stream().sorted((o1, o2) -> o1.getId().compareTo(o2.getId())).collect(Collectors.toList());
    }

    public String getUnread() {
        return comment.getDate().after(lastSeen) ? "unread" : "";
    }

    public String getOpponentName() {
        return conversation.getOpponentName(getAuthenticatedUser());
    }

    public Object onActionFromDeleteComment(Long cId) {
        getRatingService().deleteComment(cId);
        return null;
    }

}
