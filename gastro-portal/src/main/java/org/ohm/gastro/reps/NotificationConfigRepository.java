package org.ohm.gastro.reps;

import org.ohm.gastro.domain.NotificationConfigEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.service.MailService.MailType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface NotificationConfigRepository extends JpaRepository<NotificationConfigEntity, Long> {

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    NotificationConfigEntity findByUserAndMailType(UserEntity user, MailType mailType);

    @Modifying
    void deleteByUser(UserEntity user);

}
