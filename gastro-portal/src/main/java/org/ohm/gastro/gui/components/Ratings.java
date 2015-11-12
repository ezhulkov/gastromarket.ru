package org.ohm.gastro.gui.components;

import com.google.common.collect.Range;
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
        return getConversationService().findAllComments(entity).stream().filter(t -> t.getRating() != 0).findAny().isPresent();
    }

    public long getPosRatings() {
        return getConversationService().findAllComments(entity).stream().filter(t -> t.getRating() > 0).count();
    }

    public long getNegRatings() {
        return getConversationService().findAllComments(entity).stream().filter(t -> t.getRating() < 0).count();
    }

    public int getNextRating() {
        Range<Integer> ratingRange = getRatingService().getRatingRange(getCatalog().getRating());
        return ratingRange == null ? 0 : ratingRange.upperEndpoint();
    }

}
