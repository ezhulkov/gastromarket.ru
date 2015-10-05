package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Parameter;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentableEntity;
import org.ohm.gastro.domain.CommentableEntity.Type;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 30.05.15.
 */
public class Ratings extends BaseComponent {

    @Parameter(required = true, allowNull = false)
    private CommentableEntity entity;

    public CatalogEntity getCatalog() {
        return entity.getCommentableType() == Type.CATALOG ? (CatalogEntity) entity : null;
    }

    public boolean getHasRatings() {
        return getRatingService().findAllComments(entity).stream().filter(t -> t.getRating() != 0).findAny().isPresent();
    }

    public long getPosRatings() {
        return getRatingService().findAllComments(entity).stream().filter(t -> t.getRating() > 0).count();
    }

    public long getNegRatings() {
        return getRatingService().findAllComments(entity).stream().filter(t -> t.getRating() < 0).count();
    }

}
