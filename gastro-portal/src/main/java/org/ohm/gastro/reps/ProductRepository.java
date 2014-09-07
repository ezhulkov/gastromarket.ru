package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @Query("from ProductEntity where (category=:category or :category is null) and (catalog=:catalog or :catalog is null)")
    List<ProductEntity> findAllByCategoryAndCatalog(@Param("category") CategoryEntity category, @Param("catalog") CatalogEntity catalog);

}
