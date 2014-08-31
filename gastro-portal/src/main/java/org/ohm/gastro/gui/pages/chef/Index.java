package org.ohm.gastro.gui.pages.chef;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Index extends BaseComponent {

    @Property
    private CatalogEntity catalog;

    @Property
    private ProductEntity oneProduct;

    public Class onActivate(EventContext context) {
        if (context.getCount() == 0) return List.class;
        catalog = getCatalogService().findCatalog(context.get(Long.class, 0));
        return null;
    }

    @Cached
    public java.util.List<ProductEntity> getProducts() {
        return getCatalogService().findAllProducts(catalog);
    }

}
