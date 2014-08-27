package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CookEntity;
import org.ohm.gastro.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface CookRepository extends JpaRepository<CookEntity, Long> {

    List<CookEntity> findAllByUser(UserEntity user);

}
