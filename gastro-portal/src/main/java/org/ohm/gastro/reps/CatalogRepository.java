package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.UserEntity;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface CatalogRepository extends AltIdRepository<CatalogEntity> {

    List<CatalogEntity> findAllByUser(UserEntity user);

}
