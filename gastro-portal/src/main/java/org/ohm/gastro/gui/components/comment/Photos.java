package org.ohm.gastro.gui.components.comment;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;

/**
 * Created by ezhulkov on 14.02.15.
 */
public class Photos extends BaseComponent {

    @Property
    @Parameter(name = "comment")
    private CommentEntity comment;

    @Property
    private PhotoEntity photo;

    @Cached(watch = "comment")
    public List<PhotoEntity> getPhotos() {
        return getImageService().findAllPhotos(comment);
    }

}
