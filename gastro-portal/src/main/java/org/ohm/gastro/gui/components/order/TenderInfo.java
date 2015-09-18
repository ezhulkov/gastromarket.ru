package org.ohm.gastro.gui.components.order;

import org.ohm.gastro.domain.CommentEntity;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class TenderInfo extends AbstractOrder {

    public java.util.List<CommentEntity> getComments() {
        return getRatingService().findAllComments(order);
    }

}
