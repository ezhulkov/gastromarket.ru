package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 30.05.15.
 */
public class Ratings extends BaseComponent {

    @Parameter
    @Property
    private CatalogEntity catalog;

    public boolean getHasRatings() {
        return getRatingService().findAllComments(catalog).stream().filter(t -> t.getRating() != 0).findAny().isPresent();
    }

    public long getPosRatings() {
        return getRatingService().findAllComments(catalog).stream().filter(t -> t.getRating() > 0).count();
    }

    public long getNegRatings() {
        return getRatingService().findAllComments(catalog).stream().filter(t -> t.getRating() < 0).count();
    }

}
