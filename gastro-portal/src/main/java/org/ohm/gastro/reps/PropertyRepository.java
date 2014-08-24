package org.ohm.gastro.reps;

import org.ohm.gastro.domain.PropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface PropertyRepository extends JpaRepository<PropertyEntity, Long> {

}
