package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Index extends BaseComponent {

    @Property
    private String searchString;

    @Property
    private CategoryEntity oneCategory;

    @Property
    private CatalogEntity oneCook;

    @Property
    private ProductEntity oneProduct;

    @Component(id = "search", parameters = {"value=searchString"})
    private TextField searchField;

    public Object onActivate(EventContext context) {
        if (context.getCount() == 0) return null;
        return new HttpError(404, "Page not found.");
    }

    @Cached
    public List<CategoryEntity> getCategories() {
        return getCatalogService().findAllRootCategories();
    }

    public Object onSubmitFromSearchForm() {
        return null;
    }

    @Cached
    public List<CatalogEntity> getCooks() {
        return getCatalogService().findAllCatalogs().stream().limit(5).collect(Collectors.toList());
    }

    @Cached
    public List<ProductEntity> getProducts() {
        return getProductService().findPromotedProducts().stream().limit(4).collect(Collectors.toList());
    }

}
