package org.ohm.gastro.gui.pages.office.messages;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.gui.pages.AbstractPage;

import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class List extends AbstractPage {

    @Property
    private ConversationEntity conversation;

    public java.util.List<ConversationEntity> getConversations() {
        return getConversationService().findAllConversations(getAuthenticatedUserOpt().orElse(null)).stream()
                .sorted((o1, o2) -> o2.getLastActionDate().compareTo(o1.getLastActionDate()))
                .collect(Collectors.toList());
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
        return getConversationService().findLastComment(conversation).orElse(null);
    }

    public String getUnread() {
        return getConversationService().findLastComment(conversation)
                .filter(c -> conversation.getLastSeenDate(getAuthenticatedUser()).before(c.getDate())).isPresent() ? "unread" : "";
    }

    public Object[] getContext() {
        return new Object[]{conversation.getAuthor().getId(), conversation.getOpponent().getId()};
    }

}
