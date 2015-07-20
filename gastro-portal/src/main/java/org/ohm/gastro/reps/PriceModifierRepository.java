package org.ohm.gastro.reps;

import org.ohm.gastro.domain.PriceEntity;
import org.ohm.gastro.domain.PriceModifierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PriceModifierRepository extends JpaRepository<PriceModifierEntity, Long> {

    @Query("from PriceModifierEntity where entity=:entity")
    List<PriceModifierEntity> findAllByEntity(@Param("entity") PriceEntity entity);

}