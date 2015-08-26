package org.ohm.gastro.reps;

import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface OrderProductRepository extends JpaRepository<OrderProductEntity, Long> {

    public List<OrderProductEntity> findAllByOrder(OrderEntity order);

}
