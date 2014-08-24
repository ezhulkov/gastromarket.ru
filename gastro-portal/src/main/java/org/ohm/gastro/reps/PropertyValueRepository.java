package org.ohm.gastro.reps;

import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface PropertyValueRepository extends JpaRepository<PropertyValueEntity, Long> {

    public List<PropertyValueEntity> findAllByProperty(PropertyEntity property, Sort sort);

}
