package org.ohm.gastro.gui;

import org.ohm.gastro.domain.BaseEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

public interface ServiceCallback<T extends BaseEntity> {

    Class<? extends BaseComponent> deleteObject(T object);

    Class<? extends BaseComponent> updateObject(T object);

    Class<? extends BaseComponent> additionalOperation(T object);

    Class<? extends BaseComponent> addObject(T object);

    T newObject();

    T newObject(String id);

    T findObject(String id);

    void beforeSubmit();

}