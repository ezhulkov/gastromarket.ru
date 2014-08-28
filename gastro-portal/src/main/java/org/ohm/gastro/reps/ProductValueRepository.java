package org.ohm.gastro.reps;

import org.ohm.gastro.domain.ProductValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface ProductValueRepository extends JpaRepository<ProductValueEntity, Long> {

}
