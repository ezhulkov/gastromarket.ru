package org.ohm.gastro.gui.components.order;

import org.ohm.gastro.domain.CommentEntity;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class Executor extends AbstractOrder {

    public String getCookReply() {
        return getConversationService().findAllComments(order).stream()
                .filter(t -> t.getAuthor().equals(order.getCatalog().getUser()))
                .map(CommentEntity::getTextRaw)
                .findFirst().orElseGet(() -> "");
    }

}
