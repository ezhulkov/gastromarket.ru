package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;

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

    public void onActivate(Long id) {
        this.conversation = getConversationService().find(id);
    }

    public Long onPassivate() {
        return conversation == null ? null : conversation.getId();
    }

    public List<CommentEntity> getComments() {
        return getRatingService().findAllComments(conversation);
    }

    public String getOpponentName() {
        return conversation.getOpponentName(getAuthenticatedUser());
    }

}
