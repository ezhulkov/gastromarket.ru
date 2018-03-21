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

    @Query("from CommentEntity where author=:author")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<CommentEntity> findAllByAuthor(@Param("author") UserEntity author);

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

    @Query(value = "SELECT m.date as m_date, m.author_id as m_a_id, m.text as m_text, c.author_id as c_a_id, c.opponent_id as c_o_id, c.id as c_id" +
            "   FROM comment m, conversation c" +
            "   WHERE m.entity_type = 'CONVERSATION' AND m.entity_id = c.id AND " +
            "  ((m.author_id = c.author_id AND (c.opponent_last_seen < m.date or c.opponent_last_seen is null)) OR " +
            "   (m.author_id = c.opponent_id AND (c.author_last_seen < m.date or c.author_last_seen is null)))",
            nativeQuery = true)
    List<Object[]> findUnseenMessages();

}