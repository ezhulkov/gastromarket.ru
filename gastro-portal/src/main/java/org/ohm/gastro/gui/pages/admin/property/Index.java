package org.ohm.gastro.gui.pages.admin.property;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Checkbox;
import org.apache.tapestry5.corelib.components.TextField;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyEntity.Type;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.gui.AbstractServiceCallback;
import org.ohm.gastro.gui.ServiceCallback;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.EditObjectPage;

import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Index extends EditObjectPage<PropertyEntity> {

    @Property
    private PropertyValueEntity onePropertyValue;

    @Property
    private PropertyValueEntity propertyValue;

    @Component(id = "name", parameters = {"value=object?.name", "validate=maxlength=64,required"})
    private TextField nameField;

    @Component(id = "mandatory", parameters = {"value=object?.mandatory"})
    private Checkbox mandatoryField;

    @Component(id = "propertyValue", parameters = {"value=propertyValue?.value", "validate=maxlength=64,required"})
    private TextField propValueField;

    @Override
    public void activated() {
        propertyValue = new PropertyValueEntity();
    }

    @Override
    public ServiceCallback<PropertyEntity> getServiceCallback() {
        return new AbstractServiceCallback<PropertyEntity>() {

            @Override
            public PropertyEntity findObject(String id) {
                return getPropertyService().findProperty(Long.parseLong(id));
            }

            @Override
            public Class<? extends BaseComponent> deleteObject(PropertyEntity object) {
                getPropertyService().deleteProperty(object.getId());
                return List.class;
            }

            @Override
            public Class<? extends BaseComponent> updateObject(PropertyEntity object) {
                getPropertyService().saveProperty(object);
                return Index.class;
            }
        };
    }

    @Cached
    public java.util.List<PropertyValueEntity> getValues() {
        return getPropertyService().findAllRootValues(getObject());
    }

    public boolean isList() {
        return getObject().getType() == Type.LIST;
    }

    public void onSubmitFromValueForm() {
        propertyValue.setProperty(getObject());
        getPropertyService().savePropertyValue(propertyValue);
    }

    public void onActionFromDelete(Long id) {
        getPropertyService().deletePropertyValue(id);
    }

    public String getChildren() {
        return getPropertyService().findAllChildrenValues(onePropertyValue).stream().map(PropertyValueEntity::getName).collect(Collectors.joining(", "));
    }

}
