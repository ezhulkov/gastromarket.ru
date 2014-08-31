package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class List extends BaseComponent {

    @Property
    private ProductEntity oneProduct;

    @Property
    private CategoryEntity category;

    public boolean onActivate() {
        category = null;
        return true;
    }

    public boolean onActivate(Long cid) {
        category = getCatalogService().findCategory(cid);
        return true;
    }

    public java.util.List<ProductEntity> getProducts() {
        return getCatalogService().findAllProducts(category);
    }

    public Object[] onPassivate() {
        return category == null ? null : new Object[]{category.getId()};
    }

}
