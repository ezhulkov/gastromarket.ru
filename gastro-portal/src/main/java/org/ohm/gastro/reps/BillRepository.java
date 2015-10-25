package org.ohm.gastro.reps;

import org.ohm.gastro.domain.BillEntity;
import org.ohm.gastro.domain.BillEntity.Status;
import org.ohm.gastro.domain.CatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface BillRepository extends JpaRepository<BillEntity, Long> {

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<BillEntity> findAllByCatalogOrderByBillNumber(CatalogEntity catalog);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<BillEntity> findAllByCatalogAndStatusOrderByBillNumber(CatalogEntity catalog, Status status);

}
