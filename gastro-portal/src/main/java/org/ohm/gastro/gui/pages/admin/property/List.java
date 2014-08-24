package org.ohm.gastro.gui.pages.admin.property;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Select;
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
public class List extends EditObjectPage<PropertyEntity> {

    @Property
    private PropertyEntity property;

    @InjectService("catalogService")
    private CatalogService catalogService;

    @Component(id = "name", parameters = {"value=object?.name", "validate=maxlength=64,required"})
    private TextField nameField;

    @Component(id = "type", parameters = {"value=object?.type", "validate=required"})
    private Select typeField;

    @Cached
    public java.util.List getProperties() {
        return catalogService.findAllProperties();
    }

    @Override
    public ServiceCallback<PropertyEntity> getServiceCallback() {
        return new AbstractServiceCallback<PropertyEntity>() {

            @Override
            public Class<? extends BaseComponent> addObject(PropertyEntity property) {
                catalogService.saveProperty(property);
                return List.class;
            }

            @Override
            public PropertyEntity newObject() {
                return new PropertyEntity();
            }

        };
    }

    public void onActionFromDelete(Long id) {
        catalogService.deleteProperty(id);
    }

}
