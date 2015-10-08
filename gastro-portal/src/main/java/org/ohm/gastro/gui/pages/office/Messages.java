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
public class Messages extends BaseComponent {

    @Property
    @Inject
    private Block messagesBlock;

    @Property
    private ConversationEntity conversation;

    public List<ConversationEntity> getConversations() {
        return getConversationService().findAllConversations(getAuthenticatedUserOpt().orElse(null));
    }

    public String getOpponentLink() {
        return getAuthenticatedUserOpt().map(conversation::getOpponentLink).orElse(null);
    }

    public String getOpponentAvatar() {
        return getAuthenticatedUserOpt().map(conversation::getOpponentAvatar).orElse(null);
    }

    public String getOpponentName() {
        return getAuthenticatedUserOpt().map(conversation::getOpponentName).orElse(null);
    }

    public CommentEntity getLastComment() {
        return getRatingService().findAllComments(conversation).stream().findFirst().orElse(null);
    }

    public String getUnread() {
        return getLastComment() != null && conversation.getLastSeenDate().before(getLastComment().getDate()) ? "unread" : "";
    }

}
