package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;

import java.util.Set;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class CooksViewed extends AbstractOrder {

    @Property
    private CatalogEntity oneCook;

    public java.util.List<CatalogEntity> getCooksViewed() {
        final Set<Long> ids = order.getCookViewsAsSet();
        return getCatalogService().findAllCatalogs(ids);
    }

}
