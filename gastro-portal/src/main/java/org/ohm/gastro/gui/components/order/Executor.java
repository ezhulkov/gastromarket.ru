package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CommentEntity;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class Executor extends AbstractOrder {

    @Property
    @Parameter(name = "additionalClass", required = false, allowNull = true, defaultPrefix = BindingConstants.LITERAL)
    private String additionalClass;

    public String getCookReply() {
        return getConversationService().findAllComments(order).stream()
                .filter(t -> t.getAuthor().equals(order.getCatalog().getUser()))
                .map(CommentEntity::getTextRaw)
                .findFirst().orElseGet(() -> "");
    }

}
