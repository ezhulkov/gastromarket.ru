package org.ohm.gastro.gui.pages;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.ohm.gastro.domain.BaseEntity;
import org.ohm.gastro.gui.ServiceCallback;
import org.ohm.gastro.gui.components.EditObject;
import org.ohm.gastro.gui.components.EditObject.FormEvent;

public abstract class EditObjectPage<T extends BaseEntity> extends AbstractPage {

    private FormEvent formEvent = null;
    private String objectId;
    private String targetObjectId;

    @Component(parameters = {"serviceCallback=serviceCallback"})
    private EditObject<T> editObject;

    public abstract ServiceCallback<T> getServiceCallback();

    public void activated() {

    }

    public final T getObject() {
        return editObject.getObject();
    }

    public FormEvent getFormEvent() {
        return formEvent;
    }

    public EditObject<T> getEditObject() {
        return editObject;
    }

    public boolean onActivate(FormEvent formEvent, String objectId) {
        this.formEvent = formEvent;
        this.editObject.setFormEvent(formEvent);
        if (FormEvent.UPDATE.equals(formEvent)) {
            this.objectId = objectId;
            this.editObject.setObject(getServiceCallback().findObject(this.objectId));
            if (this.editObject.getObject() != null) {
                activated();
            } else {
                logger.error("Object with id {} is null", this.objectId);
            }
        } else if (FormEvent.DELETE.equals(formEvent)) {
            this.objectId = objectId;
            this.editObject.setObject(getServiceCallback().findObject(this.objectId));
            if (this.editObject.getObject() != null) {
                activated();
            } else {
                logger.error("Object with id {} is null", this.objectId);
            }
        } else if (FormEvent.ADD_FOR.equals(formEvent)) {
            this.targetObjectId = objectId;
            this.editObject.setObject(getServiceCallback().newObject());
            activated();
        } else if (FormEvent.ADD.equals(formEvent)) {
            if (StringUtils.isEmpty(objectId)) {
                onActivate(formEvent);
            } else {
                this.objectId = objectId;
                this.editObject.setObject(getServiceCallback().newObject(this.objectId));
                if (this.editObject.getObject() != null) {
                    activated();
                } else {
                    logger.error("Object with id {} is null", this.objectId);
                }
            }
        }
        return true;
    }

    public boolean onActivate(Long id) {
        this.formEvent = FormEvent.SHOW;
        this.editObject.setObject(getServiceCallback().findObject(id.toString()));
        this.editObject.setFormEvent(formEvent);
        activated();
        return true;
    }

    public boolean onActivate(FormEvent formEvent) {
        if (FormEvent.ADD.equals(formEvent)) {
            this.formEvent = formEvent;
            this.editObject.setObject(getServiceCallback().newObject());
            this.editObject.setFormEvent(formEvent);
            activated();
        }
        return true;
    }

    public boolean onActivate() {
        return onActivate(FormEvent.ADD);
    }

    public Object[] onPassivate() {
        if (objectId == null) {
            if (targetObjectId == null) {
                return formEvent == null || formEvent.equals(FormEvent.ADD) ? null : new Object[]{formEvent};
            } else {
                return new Object[]{formEvent, targetObjectId};
            }
        } else {
            return new Object[]{formEvent, objectId};
        }
    }

    public String getTargetObjectId() {
        return targetObjectId;
    }

    public String getObjectId() {
        return objectId;
    }

    @Cached
    public boolean isAddAction() {
        return FormEvent.ADD.equals(formEvent);
    }

    @Cached
    public boolean isAddForAction() {
        return FormEvent.ADD_FOR.equals(formEvent);
    }

    @Cached
    public boolean isUpdateAction() {
        return FormEvent.UPDATE.equals(formEvent);
    }

    @Cached
    public boolean isDeleteAction() {
        return FormEvent.DELETE.equals(formEvent);
    }

    public String getActivationContext() {
        return FormEvent.ADD_FOR.equals(formEvent)
                ? (formEvent + "/" + targetObjectId)
                : (formEvent + "/" + objectId);
    }

    public Object[] getActivationContextArray() {
        return onPassivate();
    }

    public String getAddControlContext() {
        return getEditObject().getAddControlContext();
    }

}