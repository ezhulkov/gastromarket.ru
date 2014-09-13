package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.components.Catalog;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class List extends BaseComponent {

    @Property
    private ProductEntity oneProduct;

    @Property
    private CategoryEntity category;

    @Component(id = "catalog")
    private Catalog catalogComponent;

    public boolean onActivate() {
        category = null;
        catalogComponent.activate(null, null, null, false);
        return true;
    }

    public boolean onActivate(Long cid) {
        category = getCatalogService().findCategory(cid);
        catalogComponent.activate(null, category, null, false);
        return true;
    }

    public Object[] onPassivate() {
        return category == null ? null : new Object[]{category.getId()};
    }

}
