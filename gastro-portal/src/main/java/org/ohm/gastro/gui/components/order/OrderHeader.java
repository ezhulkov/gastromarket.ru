package org.ohm.gastro.gui.components.order;

import org.ohm.gastro.domain.CommentEntity;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class OrderHeader extends AbstractOrder {

    public java.util.List<CommentEntity> getComments() {
        return getConversationService().findAllComments(order);
    }

    public int getBasketTotal() {
        return getShoppingCart().getCatalogPrice(catalog);
    }

}
