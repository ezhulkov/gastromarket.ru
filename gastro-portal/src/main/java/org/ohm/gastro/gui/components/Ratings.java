package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;

/**
 * Created by ezhulkov on 30.05.15.
 */
public class Ratings extends BaseComponent {

    @Parameter
    @Property
    private CatalogEntity catalog;

    @Parameter
    @Property
    private UserEntity customer;

    public boolean getHasRatings() {
        final List<CommentEntity> comments = customer == null ? getRatingService().findAllComments(catalog) : getRatingService().findAllComments(customer);
        return comments.stream().filter(t -> t.getRating() != 0).findAny().isPresent();
    }

    public long getPosRatings() {
        final List<CommentEntity> comments = customer == null ? getRatingService().findAllComments(catalog) : getRatingService().findAllComments(customer);
        return comments.stream().filter(t -> t.getRating() > 0).count();
    }

    public long getNegRatings() {
        final List<CommentEntity> comments = customer == null ? getRatingService().findAllComments(catalog) : getRatingService().findAllComments(customer);
        return comments.stream().filter(t -> t.getRating() < 0).count();
    }

}
