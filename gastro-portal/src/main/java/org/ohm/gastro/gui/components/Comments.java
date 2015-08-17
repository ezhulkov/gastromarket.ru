package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;

/**
 * Created by ezhulkov on 14.02.15.
 */
public class Comments extends BaseComponent {

    @Property
    @Parameter
    private List<CommentEntity> comments;

    @Property
    private CommentEntity comment;

}
