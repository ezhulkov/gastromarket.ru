package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.PurchaseEntity;
import org.ohm.gastro.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface PurchaseRepository extends JpaRepository<PurchaseEntity, Long> {

    @Query("select p from PurchaseEntity p " +
            "join p.products pp " +
            "join pp.product pr " +
            "join pr.catalog c " +
            "where p.customer=:customer and c=:catalog " +
            "order by p.date desc")
    List<PurchaseEntity> findAllByCatalogAndCustomer(@Param("customer") UserEntity customer, @Param("catalog") CatalogEntity catalog);

}
