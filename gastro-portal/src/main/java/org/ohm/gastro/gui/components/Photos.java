package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OfferEntity;
import org.ohm.gastro.domain.OrderEntity;
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
    @Parameter(name = "order")
    private OrderEntity order;

    @Property
    @Parameter(name = "offer")
    private OfferEntity offer;

    @Property
    private PhotoEntity photo;

    public List<PhotoEntity> getPhotos() {
        return comment != null ?
                getPhotoService().findAllPhotos(comment) :
                order != null ?
                        getPhotoService().findAllPhotos(order) :
                        getPhotoService().findAllPhotos(offer);
    }

}
