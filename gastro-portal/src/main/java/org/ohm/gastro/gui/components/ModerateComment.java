package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 30.05.15.
 */
public class ModerateComment extends BaseComponent {

    @Parameter
    @Property
    private CommentEntity comment;

    @Property
    private String replyText;

    public void beginRender() {
        replyText = comment.getText();
    }

    public void onSubmitFromEditForm(Long cId) {
        final CommentEntity comment = getRatingService().findComment(cId);
        comment.setText(replyText);
        getRatingService().saveComment(comment);
    }

}
