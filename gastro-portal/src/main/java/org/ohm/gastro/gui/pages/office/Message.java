package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Message extends BaseComponent {

    @Property
    private ConversationEntity conversation;

    @Property
    private CommentEntity comment;

    @Property
    private PhotoEntity photo;

    @Property
    @Inject
    private Block messagesBlock;

    @Property
    private String text;

    @Property
    private List<CommentEntity> comments;

    private Date lastSeen;

    public Object onActivate(String newConversation, Long id) {
        final UserEntity opponent = getUserService().findUser(id);
        conversation = getConversationService().findOrCreateConversation(getAuthenticatedUser(), opponent);
        return getPageLinkSource().createPageRenderLinkWithContext(Message.class, conversation.getId());
    }

    public boolean onActivate(Long id) {
        conversation = getConversationService().find(id);
        comments = getConversationService().findAllComments(conversation);
        if (!isAdmin()) {
            Optional<CommentEntity> lastComment = getConversationService().findLastComment(conversation);
            lastSeen = lastComment.map(t -> t.getAuthor().equals(getAuthenticatedUser()) ? new Date() : conversation.getLastSeenDate()).orElse(new Date());
            lastComment.filter(t -> !t.getAuthor().equals(getAuthenticatedUser())).ifPresent(t -> {
                conversation.setLastSeenDate(t.getDate());
                getConversationService().save(conversation);
            });
        }
        return false;
    }

    public Long onPassivate() {
        return conversation == null ? null : conversation.getId();
    }

    public String getUnread() {
        return !comment.getAuthor().equals(getAuthenticatedUser()) && comment.getDate().after(lastSeen) ? "unread" : "";
    }

    public String getOpponentName() {
        return conversation.getOpponentName(getAuthenticatedUser());
    }

    public Object onActionFromDeleteComment(Long cId) {
        getConversationService().deleteComment(cId);
        return null;
    }

    public Block onActionFromUploadPhotoAjaxLink(Long cId) {
        conversation = getConversationService().find(cId);
        comments = getConversationService().findAllComments(conversation);
        return messagesBlock;
    }

    public Block onSubmitFromPostForm(Long cid) {
        final CommentEntity comment = new CommentEntity();
        comment.setText(text);
        getConversationService().placeComment(conversation, comment, getAuthenticatedUser());
        comments = getConversationService().findAllComments(conversation);
        return messagesBlock;
    }

    public boolean isCommentOwner() {
        return comment.getAuthor().equals(getAuthenticatedUser());
    }

}
