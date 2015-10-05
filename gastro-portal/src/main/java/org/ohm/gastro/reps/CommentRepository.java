package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.CommentableEntity;
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

    @Query("from CommentEntity where entity=:entity order by id desc")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<CommentEntity> findAllByEntity(@Param("entity") CommentableEntity entity);

    @Query("from CommentEntity where entity=:entity and author=:author order by id desc")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<CommentEntity> findAllByEntityAndAuthor(@Param("entity") CommentableEntity entity, @Param("author") UserEntity author);

}