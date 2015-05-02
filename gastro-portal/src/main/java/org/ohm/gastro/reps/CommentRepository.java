package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findAllByCatalogOrderByIdDesc(CatalogEntity catalog);

    @Query("from CommentEntity where catalog=:catalog and rating!=0")
    List<CommentEntity> findAllRatings(@Param("catalog") CatalogEntity catalog);

}
