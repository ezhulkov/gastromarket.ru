package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface CatalogRepository extends JpaRepository<CatalogEntity, Long> {

    List<CatalogEntity> findAllByUser(UserEntity user);

}
