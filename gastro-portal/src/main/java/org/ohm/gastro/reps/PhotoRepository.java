package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OfferEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface PhotoRepository extends JpaRepository<PhotoEntity, Long> {

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PhotoEntity> findAllByOrder(OrderEntity order);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PhotoEntity> findAllByOffer(OfferEntity offer);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PhotoEntity> findAllByComment(CommentEntity comment);

}
