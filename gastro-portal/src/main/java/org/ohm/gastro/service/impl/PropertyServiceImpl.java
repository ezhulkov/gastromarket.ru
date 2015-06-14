package org.ohm.gastro.service.impl;

import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.reps.PropertyRepository;
import org.ohm.gastro.reps.PropertyValueRepository;
import org.ohm.gastro.service.PropertyService;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
@Component("propertyService")
@Transactional
public class PropertyServiceImpl implements PropertyService, Logging {

    private final PropertyRepository propertyRepository;
    private final PropertyValueRepository propertyValueRepository;

    @Autowired
    public PropertyServiceImpl(PropertyRepository propertyRepository,
                               PropertyValueRepository propertyValueRepository) {
        this.propertyRepository = propertyRepository;
        this.propertyValueRepository = propertyValueRepository;
    }

    @Override
    public List<PropertyEntity> findAllProperties() {
        return propertyRepository.findAll(new Sort("type", "name"));
    }

    @Override
    public List<PropertyEntity> findAllProperties(boolean mandatory) {
        return propertyRepository.findAllByMandatory(mandatory);
    }

    @Override
    public List<PropertyValueEntity> findAllRootValues(PropertyEntity property) {
        return propertyValueRepository.findAllByPropertyAndRootValue(property, true, new Sort(Direction.ASC, "value"));
    }

    @Override
    public List<PropertyValueEntity> findAllLeafValues(PropertyEntity property) {
        return propertyValueRepository.findAllByPropertyAndRootValue(property, false, new Sort(Direction.ASC, "value"));
    }

    @Override
    public List<PropertyValueEntity> findAllValues(PropertyEntity property) {
        return propertyValueRepository.findAllByProperty(property, new Sort(Direction.ASC, "value"));
    }

    @Override
    public List<PropertyValueEntity> findAllChildrenValues(final PropertyValueEntity value) {
        return propertyValueRepository.findAllChildrenValues(value, new Sort(Direction.ASC, "value"));
    }


    @Override
    public PropertyEntity findProperty(Long id) {
        return propertyRepository.findOne(id);
    }

    @Override
    public PropertyValueEntity findPropertyValue(Long id) {
        return propertyValueRepository.findOne(id);
    }

    @Override
    public PropertyValueEntity findPropertyValue(String altId) {
        return findByAltId(altId, propertyValueRepository);
    }

    @Override
    public PropertyEntity saveProperty(PropertyEntity property) {
        return propertyRepository.save(property);
    }

    @Override
    public PropertyValueEntity savePropertyValue(PropertyValueEntity value) {
        value.setAltId(value.transliterate());
        return propertyValueRepository.save(value);
    }

    @Override
    public void deleteProperty(Long id) {
        propertyRepository.delete(id);
    }

    @Override
    public void deletePropertyValue(Long id) {
        propertyValueRepository.delete(id);
    }

    @Override
    public List<PropertyValueEntity> findAllValues(PropertyValueEntity.Tag tag) {
        return propertyValueRepository.findAllByTag(tag, new Sort(Direction.ASC, "id"));
    }

    @Override
    public void attachPropertyValue(PropertyValueEntity parent, PropertyValueEntity child) {
        child.setProperty(parent.getProperty());
        child.getParents().add(parent);
        child.setRootValue(false);
        propertyValueRepository.save(child);
    }

    @Override
    public void detachPropertyValue(PropertyValueEntity parent, PropertyValueEntity child) {
        parent.getChildren().remove(child);
        child.getParents().remove(parent);
        propertyValueRepository.save(parent);
        propertyValueRepository.save(child);
    }

}
