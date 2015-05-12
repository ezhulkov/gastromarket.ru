package org.ohm.gastro.reps;

import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.PropertyValueEntity.Tag;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface PropertyValueRepository extends JpaRepository<PropertyValueEntity, Long> {

    List<PropertyValueEntity> findAllByProperty(PropertyEntity property, Sort sort);

    List<PropertyValueEntity> findAllByTag(Tag tag);

}
