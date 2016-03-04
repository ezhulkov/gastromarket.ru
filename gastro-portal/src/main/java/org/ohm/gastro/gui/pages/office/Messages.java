package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.gui.pages.AbstractPage;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Messages extends AbstractPage {

    @Property
    private ConversationEntity conversation;

    public List<ConversationEntity> getConversations() {
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
        return "read";
//        return getConversationService().findLastComment(conversation)
//                .map(t -> !t.getAuthor().equals(getAuthenticatedUser()) && conversation.getLastSeenDate().before(t.getDate()))
//                .map(t -> t ? "unread" : "")
//                .orElse("");
    }

    public Object[] getContext() {
        return new Object[]{conversation.getAuthor().getId(), conversation.getOpponent().getId()};
    }

}
