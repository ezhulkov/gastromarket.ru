package org.ohm.gastro.reps;

import org.ohm.gastro.domain.BillEntity;
import org.ohm.gastro.domain.CatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface BillRepository extends JpaRepository<BillEntity, Long> {

    List<BillEntity> findByCatalogOrderByDateAsc(CatalogEntity catalog);

    BillEntity findByCatalogAndDateBetween(CatalogEntity catalog, Date start, Date end);

}
