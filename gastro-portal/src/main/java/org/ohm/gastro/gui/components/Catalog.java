package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Catalog extends BaseComponent {

    @Property
    @Parameter(name = "catalog", required = false, allowNull = true)
    private CatalogEntity catalog;

    @Property
    @Parameter(name = "category", required = false, allowNull = true)
    private CategoryEntity category;

    @Inject
    @Property
    private Block productsBlock;

    @Component
    private Filter filter;

    @Property
    @Component
    private Zone productsZone;

    @Property
    private ProductEntity oneProduct;

    public void activate(CatalogEntity catalog, CategoryEntity category) {
        filter.activate(catalog, category);
    }

    public CategoryEntity getSelectedCategory() {
        return filter.getCategory();
    }

    public List<ProductEntity> getProducts() {
        return getCatalogService().findAllProducts(filter.getCategory(), filter.getCatalog());
    }

    public boolean isCatalogOwner() {
        UserEntity user = getAuthenticatedUser();
        return user != null && catalog != null && catalog.getUser().equals(user);
    }

    public void onActionFromDeleteProduct(Long id) {
        getCatalogService().deleteProduct(id);
    }

}

