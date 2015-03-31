package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.UserEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface CatalogRepository extends AltIdRepository<CatalogEntity> {

    List<CatalogEntity> findAllByUser(UserEntity user);

    @Query("select distinct c from CatalogEntity c join c.user u join c.products p " +
            "where c.wasSetup=true and u.status='ENABLED' and size(p)>0")
    List<CatalogEntity> findAllActive();

}
