package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.filter.RegionFilter;
import org.ohm.gastro.gui.pages.AbstractPage;

import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class List extends AbstractPage {

    @Property
    private CatalogEntity oneCatalog;

    public Object onActivate(EventContext context) {
        if (context.getCount() == 0) return null;
        return new HttpError(404, "Page not found.");
    }

    @Cached
    public java.util.List<CatalogEntity> getCatalogs() {
        return getCatalogService().findAllActiveCatalogs(RegionFilter.getCurrentRegion()).stream()
                .filter(t -> Boolean.TRUE.equals(t.getContractSigned()))
                .filter(t -> getProductService().findProductsForFrontendCount(t) > 0)
                .collect(Collectors.toList());
    }

    public String getRootProperties() {
        return getProductService().findAllRootValues(oneCatalog, true).stream()
                .map(PropertyValueEntity::getName)
                .distinct()
                .collect(Collectors.joining(", "));
    }

}
