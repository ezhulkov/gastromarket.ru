package org.ohm.gastro.gui.pages.admin.property;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.gui.AbstractServiceCallback;
import org.ohm.gastro.gui.ServiceCallback;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.EditObjectPage;
import org.ohm.gastro.service.CatalogService;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Index extends EditObjectPage<PropertyEntity> {

    @Property
    private PropertyEntity property;

    @InjectService("catalogService")
    private CatalogService catalogService;

    @Component(id = "name", parameters = {"value=object?.name", "validate=maxlength=64,required"})
    private TextField nameField;

    @Override
    public ServiceCallback<PropertyEntity> getServiceCallback() {
        return new AbstractServiceCallback<PropertyEntity>() {

            @Override
            public PropertyEntity findObject(String id) {
                return catalogService.findProperty(Long.parseLong(id));
            }

            @Override
            public Class<? extends BaseComponent> deleteObject(PropertyEntity object) {
                catalogService.deleteProperty(object.getId());
                return List.class;
            }

            @Override
            public Class<? extends BaseComponent> updateObject(PropertyEntity object) {
                catalogService.saveProperty(object);
                return Index.class;
            }
        };
    }

}
