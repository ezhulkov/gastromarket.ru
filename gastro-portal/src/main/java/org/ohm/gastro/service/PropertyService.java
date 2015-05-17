package org.ohm.gastro.service;

import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.reps.PropertyValueRepository;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface PropertyService extends AltIdService<PropertyValueEntity, PropertyValueRepository> {

    List<PropertyEntity> findAllProperties();

    List<PropertyEntity> findAllProperties(boolean mandatory);

    PropertyEntity findProperty(Long id);

    PropertyValueEntity findPropertyValue(Long id);

    PropertyValueEntity findPropertyValue(String altId);

    PropertyEntity saveProperty(PropertyEntity property);

    PropertyValueEntity savePropertyValue(PropertyValueEntity value);

    void deleteProperty(Long id);

    void deletePropertyValue(Long id);

    List<PropertyValueEntity> findAllRootValues(PropertyEntity property);

    List<PropertyValueEntity> findAllLeafValues(PropertyEntity property);

    List<PropertyValueEntity> findAllValues(PropertyValueEntity.Tag root);

    void attachPropertyValue(PropertyValueEntity parent, PropertyValueEntity child);

    void detachPropertyValue(PropertyValueEntity parent, PropertyValueEntity child);

    List<PropertyValueEntity> findAllChildrenValues(PropertyValueEntity object);

}
