package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<CommentEntity> findAllByCatalogOrderByIdDesc(CatalogEntity catalog);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<CommentEntity> findAllByUserOrderByIdDesc(UserEntity user);

    @Query("from CommentEntity where catalog=:catalog and rating!=0")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<CommentEntity> findAllRatings(@Param("catalog") CatalogEntity catalog);

}
