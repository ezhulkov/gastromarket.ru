package org.ohm.gastro.gui.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Submit;
import org.ohm.gastro.domain.BaseEntity;
import org.ohm.gastro.gui.ServiceCallback;
import org.ohm.gastro.gui.mixins.BaseComponent;

public class EditObject<T extends BaseEntity> extends BaseComponent {

    public enum FormEvent {
        DELETE, UPDATE, ADD, ADD_FOR, ADD_CONTROL, SHOW
    }

    private FormEvent formEvent;
    private String addControlContext = "";

    @Parameter(name = "serviceCallback", required = true)
    private ServiceCallback<T> serviceCallback;

    @Property
    @Parameter(name = "showControls", value = "true")
    private boolean showControls;

    @Parameter(name = "showDelete", value = "false")
    private boolean showDelete;

    @Property
    @Parameter(name = "showUpdate", value = "false")
    private boolean showUpdate;

    @Property
    @Parameter(name = "showAdd", value = "false")
    private boolean showAdd;

    @Property
    @Parameter(name = "autoFocus", defaultPrefix = BindingConstants.LITERAL, value = "true")
    private boolean autoFocus;

    @Property
    @Parameter(name = "addControls", defaultPrefix = BindingConstants.LITERAL)
    private Block addControls;

    private T object;

    @Component(id = "add")
    private Submit addBtn;

    @Component(id = "save")
    private Submit saveBtn;

    @Component(id = "delete")
    private Submit deleteBtn;

    @Component
    private Form form;

    public void setObject(T object) {
        this.object = object;
    }

    public T getObject() {
        return object;
    }

    public void onSelectedFromAdd() {
        setFormEvent(FormEvent.ADD);
    }

    public void onSelectedFromSave() {
        setFormEvent(FormEvent.UPDATE);
    }

    public void onSelectedFromDelete() {
        setFormEvent(FormEvent.DELETE);
    }

    public FormEvent getFormEvent() {
        return formEvent;
    }

    public void setFormEvent(FormEvent formEvent) {
        this.formEvent = formEvent;
    }

    public String getAddControlContext() {
        return addControlContext;
    }

    public void setAddControlContext(String addControlContext) {
        this.addControlContext = addControlContext;
    }

    public String getUpdateLabel() {
        if (FormEvent.ADD.equals(getFormEvent()) || FormEvent.ADD_FOR.equals(getFormEvent())) {
            return getMessages().get("create-label");
        } else {
            return getMessages().get("save-label");
        }
    }

    public Object onSubmitFromForm() {
        if (FormEvent.DELETE.equals(getFormEvent())) {
            return serviceCallback.deleteObject(getObject());
        } else if (!form.getHasErrors() && FormEvent.ADD_CONTROL.equals(getFormEvent())) {
            return serviceCallback.additionalOperation(getObject());
        } else if (!form.getHasErrors() && FormEvent.UPDATE.equals(getFormEvent())) {
            return serviceCallback.updateObject(getObject());
        } else if (!form.getHasErrors() && FormEvent.ADD.equals(getFormEvent())) {
            return serviceCallback.addObject(getObject());
        }
        return null;
    }

    public boolean isShowDelete() {
        return !FormEvent.ADD.equals(getFormEvent()) && !FormEvent.ADD_FOR.equals(getFormEvent()) && showDelete;
    }

    public boolean isAddForAction() {
        return FormEvent.ADD_FOR.equals(getFormEvent());
    }

    public boolean isUpdateAction() {
        return FormEvent.UPDATE.equals(getFormEvent());
    }

    public Form getForm() {
        return form;
    }

    void onPrepareForSubmitFromForm() {
        serviceCallback.beforeSubmit();
    }

}