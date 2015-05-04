package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class List extends BaseComponent {

    @Property
    private CatalogEntity oneCatalog;

    @Property
    private ProductEntity oneProduct;

    @Property
    private CategoryEntity oneCategory;

    public Object onActivate(EventContext context) {
        if (context.getCount() == 0) return null;
        return new HttpError(404, "Page not found.");
    }

    @Cached
    public java.util.List<CatalogEntity> getCatalogs() {
        return getCatalogService().findAllActiveCatalogs();
    }

    public java.util.List<ProductEntity> getProducts() {
        return getProductService().findProductsForFrontend(null, oneCatalog, null, null, 0, 3);
    }

    public String getCategories() {
        return getProductService().findProductsForFrontend(null, oneCatalog, null, null, 0, Integer.MAX_VALUE).stream()
                .map(ProductEntity::getCategory)
                .map(t -> t.getParent() == null ? t : t.getParent())
                .distinct()
                .sorted((o1, o2) -> o1.getId().compareTo(o2.getId()))
                .map(CategoryEntity::getName)
                .collect(Collectors.joining(", "));
    }

}
