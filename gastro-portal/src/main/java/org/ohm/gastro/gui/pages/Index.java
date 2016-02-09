package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.PropertyValueEntity.Tag;
import org.ohm.gastro.filter.RegionFilter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Index extends AbstractPage {

    @Property
    private CatalogEntity oneCook;

    @Property
    private PropertyValueEntity onePropertyValue;

    @Property
    private ProductEntity oneProduct;

    public Object onActivate(EventContext context) {
        if (context.getCount() == 0) return null;
        return new HttpError(404, "Page not found.");
    }

    @Cached
    public List<CatalogEntity> getCooks() {
        return getCatalogService().findAllActiveCatalogs().stream()
                .sorted(((o1, o2) -> o2.getRating().compareTo(o1.getRating())))
                .limit(5).collect(Collectors.toList());
    }

    @Cached
    public List<ProductEntity> getProducts() {
        return getProductService().findPromotedProducts().stream().limit(4).collect(Collectors.toList());
    }

    @Cached
    public List<PropertyValueEntity> getCategoryValues() {
        return getPropertyService().findAllValues(Tag.ROOT).stream().filter(PropertyValueEntity::isMainPage).collect(Collectors.toList());
    }

    @Cached
    public List<PropertyValueEntity> getEventValues() {
        return getPropertyService().findAllValues(Tag.EVENT).stream().filter(PropertyValueEntity::isMainPage).collect(Collectors.toList());
    }

    public boolean isShowCategory() {
        return getProductService().findAllProductsCountCached(null, onePropertyValue, RegionFilter.getCurrentRegion()) > 0;
    }

    @Override
    public String getTitle() {
        return getMessages().get("page.title");
    }
}
