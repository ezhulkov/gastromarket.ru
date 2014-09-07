package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    @Query("select distinct ct from ProductEntity pt join pt.category ct where pt.catalog=:catalog")
    List<CategoryEntity> findAllByCatalog(@Param("catalog") CatalogEntity catalog);

}
