package org.ohm.gastro.gui.pages.chef;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.TextField;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.AbstractServiceCallback;
import org.ohm.gastro.gui.ServiceCallback;
import org.ohm.gastro.gui.components.Catalog;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.EditObjectPage;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Index extends EditObjectPage<CatalogEntity> {

    @Property
    private ProductEntity oneProduct;

    @Component(id = "catalog")
    private Catalog catalogComponent;

    @Component(id = "name", parameters = {"value=object?.name", "validate=maxlength=64,required"})
    private TextField nameField;

    @Override
    public void activated() {
        catalogComponent.activate(getObject(), null, null, false);
    }

    @Override
    public ServiceCallback<CatalogEntity> getServiceCallback() {
        return new AbstractServiceCallback<CatalogEntity>() {

            @Override
            public Class<? extends BaseComponent> updateObject(CatalogEntity object) {
                getCatalogService().saveCatalog(object);
                return Index.class;
            }

            @Override
            public Class<? extends BaseComponent> addObject(CatalogEntity object) {
                getCatalogService().saveCatalog(object);
                return Index.class;
            }

            @Override
            public CatalogEntity newObject() {
                CatalogEntity catalog = new CatalogEntity();
                catalog.setUser(getAuthenticatedUser());
                return catalog;
            }

            @Override
            public CatalogEntity findObject(String id) {
                return getCatalogService().findCatalog(Long.parseLong(id));
            }
        };
    }
}
