package org.ohm.gastro.gui.pages.office;

import com.google.common.collect.Lists;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.springframework.data.annotation.Persistent;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Message extends BaseComponent {

    private final static int PAGE_SIZE = 10;

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
    @Inject
    private Block messagesFetchBlock;

    @Property
    @Inject
    private Block newMessageBlock;

    @Property
    private String text;

    @Property
    private int fetchFrom = 0;

    @Property
    private int fetchTo = PAGE_SIZE;

    @Property
    @Persistent
    private List<CommentEntity> newComments = Lists.newArrayList();

    @Property
    private Date lastSeen;

    public void beginRender() {
        newComments.clear();
    }

    public Object onActivate(String newConversation, Long id) {
        final UserEntity opponent = getUserService().findUser(id);
        conversation = getConversationService().findOrCreateConversation(getAuthenticatedUser(), opponent);
        return getPageLinkSource().createPageRenderLinkWithContext(Message.class, conversation.getId());
    }

    public boolean onActivate(Long id) {
        conversation = getConversationService().find(id);
        final Optional<CommentEntity> lastComment = getConversationService().findLastComment(conversation);
        lastSeen = lastComment.map(t -> t.getAuthor().equals(getAuthenticatedUser()) ? new Date() : conversation.getLastSeenDate()).orElseGet(() -> new Date());
        if (!isAdmin()) {
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

    public String getOpponentName() {
        return conversation.getOpponentName(getAuthenticatedUser());
    }

    public void onActionFromUploadPhotoAjaxLink(Long cId) {
        conversation = getConversationService().find(cId);
        fetchFrom = 0;
        fetchTo = PAGE_SIZE;
        newComments.clear();
        getAjaxResponseRenderer()
                .addRender("allMessagesZone", messagesBlock)
                .addRender("newMessagesZone", newMessageBlock);
    }

    public Block onSubmitFromPostForm(Long cid) {
        if (text == null) return newMessageBlock;
        conversation = getConversationService().find(cid);
        CommentEntity newComment = new CommentEntity();
        newComment.setText(text);
        getConversationService().placeComment(conversation, newComment, getAuthenticatedUser());
        newComments.add(newComment);
        return newMessageBlock;
    }

    public String getMessagesZoneId() {
        return String.format("messagesZone_%s_%s", fetchFrom, fetchTo);
    }

    public boolean isShowFetch() {
        return getConversationService().findAllCommentsCount(conversation) > fetchTo;
    }

    public void onActionFromFetchMessagesAjaxLink(int from, int to) {
        this.fetchFrom = from;
        this.fetchTo = to;
        getAjaxResponseRenderer()
                .addRender(getMessagesZoneId(), messagesBlock)
                .addRender("messagesFetchZone", messagesFetchBlock);
        this.fetchFrom += to - from;
        this.fetchTo += PAGE_SIZE;
    }

    public List<CommentEntity> getComments() {
        return getConversationService().findAllComments(conversation, fetchFrom, fetchTo).stream()
                .sorted((o1, o2) -> o1.getDate().compareTo(o2.getDate()))
                .collect(Collectors.toList());
    }

}
