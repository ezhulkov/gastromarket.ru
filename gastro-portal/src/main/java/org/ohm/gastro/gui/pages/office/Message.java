package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.pages.AbstractPage;

import java.util.List;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Message extends AbstractPage {

    private final static int PAGE_SIZE = 40;

    @Property
    private ConversationEntity conversation;

    @Property
    private OrderEntity commonOrder;

    @Property
    private CommentEntity commonComment;

    public Object onActivate(Long aid, Long oid) {
        final UserEntity author = getUserService().findUser(aid);
        final UserEntity opponent = getUserService().findUser(oid);
        conversation = getConversationService().findConversation(author, opponent);
        if (!isAdmin()) {
            if (!getAuthenticatedUser().equals(author) && !getAuthenticatedUser().equals(opponent)) return new HttpError(403, "Access denied.");
        }
        final List<CommentEntity> commonComments = getOrderService().findCommonComments(conversation.getAuthor(), conversation.getOpponent());
        final List<OrderEntity> commonOrders = getOrderService().findCommonOrders(commonComments);
        commonComment = commonComments.stream().reduce((o1, o2) -> o1).orElseGet(null);
        commonOrder = commonOrders.stream().reduce((o1, o2) -> o1).orElseGet(null);
        return null;
    }

    public Object[] onPassivate() {
        return conversation == null ? null : new Object[]{conversation.getAuthor().getId(), conversation.getOpponent().getId()};
    }

    public UserEntity getOpponent() {
        return conversation.getOpponent(getAuthenticatedUser()).get();
    }

    @Cached
    public boolean isShowAttach() {
        return !commonOrder.isOrderAttached() && commonOrder.getCustomer().equals(getAuthenticatedUser());
    }

}
