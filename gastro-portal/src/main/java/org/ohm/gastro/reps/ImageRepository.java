package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface ImageRepository extends JpaRepository<PhotoEntity, Long> {

    List<PhotoEntity> findAllByOrder(OrderEntity order);

    List<PhotoEntity> findAllByComment(CommentEntity comment);

}