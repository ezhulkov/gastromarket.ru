package org.ohm.gastro.gui.pages.admin.property;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.corelib.components.TextField;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.gui.AbstractServiceCallback;
import org.ohm.gastro.gui.ServiceCallback;
import org.ohm.gastro.gui.misc.GenericSelectModel;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.EditObjectPage;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Value extends EditObjectPage<PropertyValueEntity> {

    @Property
    private PropertyValueEntity onePropertyValue;

    @Property
    private PropertyValueEntity propertyValue;

    @Component(id = "propertyValue", parameters = {"value=propertyValue?.value", "validate=maxlength=64,required"})
    private TextField propValueField;

    @Component(id = "value", parameters = {"value=object?.value", "validate=maxlength=64,required"})
    private TextField valueField;

    @Component(id = "tag", parameters = {"value=object?.tag"})
    private Select tagField;

    @Component(id = "propertyValues", parameters = {"model=valueModel", "encoder=valueModel", "value=propertyValue"})
    private Select propsField;

    @Override
    public void activated() {
        propertyValue = new PropertyValueEntity();
    }

    @Override
    public ServiceCallback<PropertyValueEntity> getServiceCallback() {
        return new AbstractServiceCallback<PropertyValueEntity>() {

            @Override
            public PropertyValueEntity findObject(String id) {
                return getPropertyService().findPropertyValue(Long.parseLong(id));
            }

            @Override
            public Class<? extends BaseComponent> deleteObject(PropertyValueEntity object) {
                getPropertyService().deletePropertyValue(object.getId());
                return Index.class;
            }

            @Override
            public Class<? extends BaseComponent> updateObject(PropertyValueEntity object) {
                getPropertyService().savePropertyValue(object);
                return Value.class;
            }
        };
    }

    @Cached
    public GenericSelectModel<PropertyValueEntity> getValueModel() {
        return new GenericSelectModel<>(getPropertyService().findAllLeafValues(getObject().getProperty()), PropertyValueEntity.class, "value", "id", getPropertyAccess());
    }

    public void onSubmitFromCreateValueForm() {
        getPropertyService().attachPropertyValue(getObject(), propertyValue);
    }

    public void onSubmitFromAttachValueForm() {
        onSubmitFromCreateValueForm();
    }

    public void onActionFromDetach(Long id) {
        getPropertyService().detachPropertyValue(getObject(), getPropertyService().findPropertyValue(id));
    }

}
