package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface PropertyRepository extends JpaRepository<PropertyEntity, Long> {

    @Query("select p from PropertyEntity p join p.categories c where c=:category")
    List<PropertyEntity> findAllProperties(@Param("category") CategoryEntity category);

    @Query("select p from PropertyEntity p join p.products prv where prv.product=:product")
    List<PropertyEntity> findAllProperties(@Param("product") ProductEntity oneProduct);

}
