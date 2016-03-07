package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
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
    private UserEntity author;

    @Parameter
    @Property
    private UserEntity opponent;

    @Property
    private OrderEntity commonOrder;

    @Property
    private CommentEntity commonComment;

    public void beginRender() {
        final List<CommentEntity> commonComments = getOrderService().findCommonComments(author, opponent);
        final List<OrderEntity> commonOrders = getOrderService().findCommonOrders(commonComments);
        commonComment = commonComments.stream().reduce((o1, o2) -> o1).orElseGet(null);
        commonOrder = commonOrders.stream().reduce((o1, o2) -> o1).orElseGet(null);
    }

    public UserEntity getOpponentUser() {
        return getAuthenticatedUser().equals(author) ? opponent : author;
    }

    @Cached
    public boolean isShowAttach() {
        return !commonOrder.isOrderAttached() && commonOrder.getCustomer().equals(getAuthenticatedUser());
    }

}