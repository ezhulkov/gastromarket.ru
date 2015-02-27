package org.ohm.gastro.reps;

import org.ohm.gastro.domain.OrderProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface OrderProductRepository extends JpaRepository<OrderProductEntity, Long> {

}
