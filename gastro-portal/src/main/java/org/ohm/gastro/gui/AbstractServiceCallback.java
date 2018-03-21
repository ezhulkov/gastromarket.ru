package org.ohm.gastro.gui;

import org.apache.commons.lang3.NotImplementedException;
import org.ohm.gastro.domain.BaseEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class AbstractServiceCallback<T extends BaseEntity> implements ServiceCallback<T> {

    @Override
    public Class<? extends BaseComponent> deleteObject(T object) {
        throw new NotImplementedException("");
    }

    @Override
    public Class<? extends BaseComponent> updateObject(T object) {
        throw new NotImplementedException("");
    }

    @Override
    public Class<? extends BaseComponent> additionalOperation(T object) {
        throw new NotImplementedException("");
    }

    @Override
    public Class<? extends BaseComponent> addObject(T object) {
        throw new NotImplementedException("");
    }

    @Override
    public T newObject() {
        throw new NotImplementedException("");
    }

    @Override
    public T newObject(String id) {
        throw new NotImplementedException("");
    }

    @Override
    public T findObject(String id) {
        throw new NotImplementedException("");
    }

    @Override
    public void beforeSubmit() {
    }

}
