package org.ohm.gastro.gui.pages.admin.property;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyEntity.Type;
import org.ohm.gastro.domain.PropertyValueEntity;
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
    private PropertyValueEntity propertyValue;

    @Property
    private String value;

    @InjectService("catalogService")
    private CatalogService catalogService;

    @Component(id = "name", parameters = {"value=object?.name", "validate=maxlength=64,required"})
    private TextField nameField;

    @Component(id = "propertyValue", parameters = {"value=propertyValue?.value", "validate=maxlength=64,required"})
    private TextField propValueField;

    @Component(id = "value", parameters = {"validate=maxlength=64,required"})
    private TextField valueField;

    @Override
    public void activated() {
        propertyValue = new PropertyValueEntity();
    }

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

    @Cached
    public java.util.List<PropertyValueEntity> getValues() {
        return catalogService.findAllValues(getObject());
    }

    public boolean isList() {
        return getObject().getType().equals(Type.LIST);
    }

    public void onSubmitFromValueForm() {
        propertyValue.setProperty(getObject());
        catalogService.savePropertyValue(propertyValue);
    }

    public void onPrepareFromValueChangeForm() {
        value = propertyValue.getValue();
    }

    public void onSubmitFromValueChangeForm(Long vid) {
        PropertyValueEntity valueEntity = catalogService.findPropertyValue(vid);
        valueEntity.setValue(value);
        valueEntity.setProperty(getObject());
        catalogService.savePropertyValue(valueEntity);
    }

    public void onActionFromDelete(Long id) {
        catalogService.deletePropertyValue(id);
    }

}
