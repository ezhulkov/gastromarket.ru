package org.ohm.gastro.reps;

import org.ohm.gastro.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

}
