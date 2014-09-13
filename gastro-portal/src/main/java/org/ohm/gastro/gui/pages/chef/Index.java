package org.ohm.gastro.gui.pages.chef;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.components.Catalog;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Index extends BaseComponent {

    @Property
    private CatalogEntity catalog;

    @Property
    private ProductEntity oneProduct;

    @Component(id = "catalog")
    private Catalog catalogComponent;

    public Class onActivate(EventContext context) {
        if (context.getCount() == 0) return List.class;
        catalog = getCatalogService().findCatalog(context.get(Long.class, 0));
        catalogComponent.activate(catalog, null, null, false);
        return null;
    }

    public Object[] onPassivate() {
        return catalog == null ? null : new Object[]{catalog.getId()};
    }

}
