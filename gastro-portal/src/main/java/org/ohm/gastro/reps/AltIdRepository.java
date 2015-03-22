package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface AltIdRepository<T extends CatalogEntity> extends JpaRepository<T, Long> {

    T findByAltId(String altId);

}
