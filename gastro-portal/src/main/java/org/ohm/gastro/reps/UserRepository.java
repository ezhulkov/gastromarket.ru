package org.ohm.gastro.reps;

import org.ohm.gastro.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    UserEntity findByEmail(String email);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<UserEntity> findAllByReferrer(UserEntity referrer);

}
