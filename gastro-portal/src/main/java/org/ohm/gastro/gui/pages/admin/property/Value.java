package org.ohm.gastro.gui.pages.admin.property;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Checkbox;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.corelib.components.TextField;
import org.ohm.gastro.domain.PropertyEntity.Type;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.gui.AbstractServiceCallback;
import org.ohm.gastro.gui.ServiceCallback;
import org.ohm.gastro.gui.misc.GenericSelectModel;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.EditObjectPage;

import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Value extends EditObjectPage<PropertyValueEntity> {

    @Property
    private PropertyValueEntity onePropertyValue;

    @Property
    private PropertyValueEntity propertyValue;

    @Component(id = "propertyValue", parameters = {"value=propertyValue?.name", "validate=maxlength=64,required"})
    private TextField propValueField;

    @Component(id = "value", parameters = {"value=object?.name", "validate=maxlength=64,required"})
    private TextField valueField;

    @Component(id = "tag", parameters = {"value=object?.tag"})
    private Select tagField;

    @Component(id = "main", parameters = {"value=object?.mainPage"})
    private Checkbox mainField;

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
        final java.util.List<PropertyValueEntity> allValues = getPropertyService()
                .findAllValues(getObject().getProperty()).stream()
                .filter(t -> t.getChildren().isEmpty())
                .filter(t -> !t.equals(getObject()))
                .filter(t -> !t.getParents().contains(getObject()))
                .collect(Collectors.toList());
        return new GenericSelectModel<>(allValues, PropertyValueEntity.class, "name", "id", getPropertyAccess());
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

    public java.util.List<PropertyValueEntity> getPropertyValues() {
        return getPropertyService().findAllChildrenValues(getObject());
    }

    public boolean isChildrenAllowed() {
        return getObject().getProperty().getType() == Type.LIST && getObject().getParents().isEmpty();
    }

}
