package org.ohm.gastro.gui.pages.admin.catalog;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.corelib.components.TextField;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.gui.AbstractServiceCallback;
import org.ohm.gastro.gui.ServiceCallback;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.EditObjectPage;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Index extends EditObjectPage<CatalogEntity> {

    @Component(id = "name", parameters = {"value=object?.name", "validate=maxlength=64,required"})
    private TextField nameField;

    @Override
    public ServiceCallback<CatalogEntity> getServiceCallback() {
        return new AbstractServiceCallback<CatalogEntity>() {

            @Override
            public CatalogEntity findObject(String id) {
                return getUserService().findCatalog(Long.parseLong(id));
            }

            @Override
            public Class<? extends BaseComponent> deleteObject(CatalogEntity object) {
                getUserService().deleteCatalog(object.getId());
                return List.class;
            }

            @Override
            public Class<? extends BaseComponent> updateObject(CatalogEntity object) {
                getUserService().saveCatalog(object);
                return Index.class;
            }
        };
    }

}
