package org.ohm.gastro.gui.components.comment;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 13.08.15.
 */
public class Edit extends BaseComponent {

    @Property
    @Parameter(allowNull = false, required = false)
    private CommentEntity comment;

    @InjectComponent
    private InjectPhotos injectPhotos;

    public void onPrepareFromForm(Long cid) {
        injectPhotos.getSubmittedPhotos().clear();
        comment = getConversationService().findComment(cid);
    }

    public void onSuccessFromForm() {
        getConversationService().saveComment(comment);
        getPhotoService().attachPhotos(comment, injectPhotos.getSubmittedPhotos());
    }

    public Object[] getFormContext() {
        return new Object[]{comment == null ? null : comment.getId()};
    }

}
