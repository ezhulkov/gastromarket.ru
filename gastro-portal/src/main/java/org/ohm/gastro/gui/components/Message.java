package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.ActionLink;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;

/**
 * Created by ezhulkov on 07.03.16.
 */
public class Message extends BaseComponent {

    @Parameter
    @Property
    private boolean modal = false;

    @Parameter
    @Property
    private UserEntity author;

    @Parameter
    @Property
    private UserEntity opponent;

    @Property
    private OrderEntity commonOrder;

    @Property
    private CommentEntity commonComment;

    @InjectComponent
    @Property
    private ActionLink delete;

    public void beginRender() {
        final List<CommentEntity> commonComments = getOrderService().findCommonComments(author, opponent);
        final List<OrderEntity> commonOrders = getOrderService().findCommonOrders(author, opponent);
        commonComment = commonComments.stream().reduce((o1, o2) -> o1).orElse(null);
        commonOrder = commonOrders.stream().reduce((o1, o2) -> o1).orElse(null);
    }

    public UserEntity getOpponentUser() {
        return getAuthenticatedUser().equals(author) ? opponent : author;
    }

    @Cached
    public boolean isShowAttach() {
        return !commonOrder.isOrderAttached() && commonOrder.getCustomer().equals(getAuthenticatedUser());
    }

    public String getContactsText() {
        return getMessages().get("contacts.text." + getAuthenticatedUserOpt().map(u -> u.getType().toString().toLowerCase()).orElseGet(() -> ""));
    }

    public void onActionFromDelete(Long id) {
        getConversationService().deleteComment(id);
    }

}