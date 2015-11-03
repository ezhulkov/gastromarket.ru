package org.ohm.gastro.gui.components.comment;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.CommentableEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 13.08.15.
 */
public abstract class AbstractModal extends BaseComponent {

    @Property
    @Parameter
    private CommentEntity comment;

    @Property
    @Parameter
    private CommentableEntity entity;

    @Property
    @Parameter
    private OrderEntity order;

    @Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String modalId;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String title;

    public String getTitle() {
        return comment != null ? getMessages().get("comment.edit") : title;
    }

}
