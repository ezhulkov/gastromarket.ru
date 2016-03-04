package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.CommentableEntity;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.domain.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    @Query("from CommentEntity where entity=:entity order by date asc")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<CommentEntity> findAllByEntityOrderByDateAsc(@Param("entity") CommentableEntity entity);

    @Query("from CommentEntity where entity=:entity order by date desc")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<CommentEntity> findAllByEntityOrderByDateDesc(@Param("entity") CommentableEntity entity);

    @Query("from CommentEntity where entity=:entity and author=:author order by id desc")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<CommentEntity> findAllByEntityAndAuthor(@Param("entity") CommentableEntity entity, @Param("author") UserEntity author);

    @Query("from CommentEntity where entity=:entity order by date desc")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<CommentEntity> findAllByEntity(@Param("entity") CommentableEntity entity, Pageable page);

    @Query("select count(*) from CommentEntity where entity=:entity")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    int countAllByEntity(@Param("entity") CommentableEntity entity);

    @Query("select count(c) from CommentEntity c where c.date>:date and c.entity=:conversation")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    int countUnreadMessages(@Param("conversation") ConversationEntity conversation, @Param("date") Date lastSeenDate);

}