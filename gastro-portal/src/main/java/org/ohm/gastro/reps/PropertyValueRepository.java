package org.ohm.gastro.reps;

import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.PropertyValueEntity.Tag;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface PropertyValueRepository extends AltIdRepository<PropertyValueEntity> {

    List<PropertyValueEntity> findAllByPropertyAndRootValue(PropertyEntity property, Boolean rootValue, Sort sort);

    List<PropertyValueEntity> findAllByTag(Tag tag);

}
