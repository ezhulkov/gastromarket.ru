package org.ohm.gastro.reps;

import org.ohm.gastro.domain.ConversationEntity;
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
public interface ConversationRepository extends JpaRepository<ConversationEntity, Long> {

    @Query("from ConversationEntity where author=:user or opponent=:user order by date")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<ConversationEntity> findAllConversations(@Param("user") UserEntity user);

    @Query("from ConversationEntity where (author=:author and opponent=:opponent) or (author=:opponent and opponent=:author) order by date")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    ConversationEntity findConversation(@Param("author") UserEntity author, @Param("opponent") UserEntity opponent);

}
