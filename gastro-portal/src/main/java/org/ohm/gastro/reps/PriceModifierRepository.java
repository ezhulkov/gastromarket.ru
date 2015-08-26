package org.ohm.gastro.reps;

import org.ohm.gastro.domain.PriceModifierEntity;
import org.ohm.gastro.domain.PurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;
import java.util.List;

public interface PriceModifierRepository extends JpaRepository<PriceModifierEntity, Long> {

    @Query("from PriceModifierEntity where entity=:entity")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PriceModifierEntity> findAllByEntity(@Param("entity") PurchaseEntity entity);

    @Modifying
    @Query("delete from PriceModifierEntity where entity=:entity")
    void deleteAllByEntity(@Param("entity") PurchaseEntity entity);

}