package org.ohm.gastro.reps;

import org.ohm.gastro.domain.MessageEntity;
import org.ohm.gastro.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    @Query("from MessageEntity where sender=:sender order by createdDate")
    List<MessageEntity> findAllSentMessages(@Param("sender") UserEntity sender);

    @Query("from MessageEntity where (recipientType='ALL') or " +
            "(recipientType='COOK' and :type='COOK') or " +
            "(recipientType='USER' and recipient=:rcpt) order by createdDate")
    List<MessageEntity> findAllReceivedMessages(@Param("rcpt") UserEntity recipient, @Param("type") UserEntity.Type type);

    @Modifying
    @Query("update MessageEntity set senderStatus='DELETED' where id=:id")
    void deleteSentMessage(@Param("id") Long id);

    @Modifying
    @Query("update MessageEntity set recipientStatus='DELETED' where id=:id")
    void deleteReceivedMessage(@Param("id") Long id);

}
