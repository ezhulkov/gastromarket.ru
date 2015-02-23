package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @Query("from ProductEntity pr " +
            "where (pr.category=:category or :category is null) and " +
            "      (pr.catalog=:catalog or :catalog is null) and " +
            "      (pr.hidden=false or :hidden is null)")
    Page<ProductEntity> findAllByCategoryAndCatalog(@Param("category") CategoryEntity category, @Param("catalog") CatalogEntity catalog, @Param("hidden") Boolean hidden, Pageable page);

    @Query("select count(*) from ProductEntity where catalog=:catalog and hidden=false")
    int findCountCatalog(@Param("catalog") CatalogEntity catalog);

    @Query("select pr from ProductEntity pr " +
            "join pr.category c " +
            "left join c.parent p " +
            "where (p=:category or c=:category) and " +
            "      (pr.hidden=false or :hidden is null)")
    Page<ProductEntity> findAllByParentCategory(@Param("category") CategoryEntity parentCategory, @Param("hidden") Boolean hidden, Pageable page);

    @Query(value = "SELECT * FROM (" +
            "       SELECT " +
            "         p.*, " +
            "         ts_rank_cd(to_tsvector('ru', (COALESCE(c.name,'') || ' ' || COALESCE(p.name,'') || ' ' || COALESCE(p.description,''))), to_tsquery(:q)) AS score " +
            "       FROM product p JOIN category c ON c.id = p.category_id " +
            "       WHERE (COALESCE(c.name,'') || ' ' || COALESCE(p.name,'') || ' ' || COALESCE(p.description,'')) @@ to_tsquery(:q) " +
            "     ) s " +
            "WHERE score > 0 " +
            "ORDER BY score DESC", nativeQuery = true)
    List<ProductEntity> searchProducts(@Param("q") String query);

    @Query("from ProductEntity where promoted=true")
    List<ProductEntity> findAllPromotedProducts();

}
