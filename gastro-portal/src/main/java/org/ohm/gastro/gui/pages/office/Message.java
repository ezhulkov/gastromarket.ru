package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.pages.AbstractPage;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Message extends AbstractPage {

    private final static int PAGE_SIZE = 40;

    @Property
    private ConversationEntity conversation;

    public Object onActivate(Long aid, Long oid) {
        final UserEntity author = getUserService().findUser(aid);
        final UserEntity opponent = getUserService().findUser(oid);
        conversation = getConversationService().findConversation(author, opponent);
        if (!isAdmin()) {
            if (!getAuthenticatedUser().equals(author) && !getAuthenticatedUser().equals(opponent)) return new HttpError(403, "Access denied.");
        }
        return null;
    }

    public Object[] onPassivate() {
        return conversation == null ? null : new Object[]{conversation.getAuthor().getId(), conversation.getOpponent().getId()};
    }

    public UserEntity getOpponent() {
        return conversation.getOpponent(getAuthenticatedUser()).get();
    }

    @Cached
    public OrderEntity getCommonOrder() {
        return getOrderService().findCommonOrder(conversation.getAuthor(), conversation.getOpponent());
    }

}
