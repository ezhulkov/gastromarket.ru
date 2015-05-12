package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface ProductRepository extends AltIdRepository<ProductEntity> {

//    @Query("from ProductEntity pr " +
//            "where (pr.category=:category or :category is null) and " +
//            "      (pr.catalog=:catalog or :catalog is null) and " +
//            "      (pr.wasSetup=true or :wasSetup is null)")
//    Page<ProductEntity> findAllByPropertyAndCatalog(@Param("category") CategoryEntity category, @Param("catalog") CatalogEntity catalog, @Param("wasSetup") Boolean wasSetup, Pageable page);

    @Query("select count(*) from ProductEntity where catalog=:catalog and wasSetup=false")
    int findCountCatalog(@Param("catalog") CatalogEntity catalog);

//    @Query("select pr from ProductEntity pr " +
//            "join pr.category c " +
//            "left join c.parent p " +
//            "where (p=:category or c=:category) and " +
//            "      (pr.wasSetup=true or :wasSetup is null)")
//    Page<ProductEntity> findAllByParentCategory(@Param("category") CategoryEntity parentCategory, @Param("wasSetup") Boolean wasSetup, Pageable page);

    @Query(value = "SELECT *\n" +
            "FROM (\n" +
            "       SELECT\n" +
            "         P.*,\n" +
            "         TS_RANK_CD(TO_TSVECTOR('RU', (COALESCE(C.NAME, '') || ' ' || COALESCE(P.NAME, '') || ' ' || COALESCE(P.DESCRIPTION, ''))), TO_TSQUERY(:q)) AS SCORE\n" +
            "       FROM PRODUCT P JOIN CATEGORY C ON C.ID = P.CATEGORY_ID\n" +
            "         LEFT JOIN CATEGORY PC ON PC.ID = C.PARENT_ID\n" +
            "       WHERE LOWER((COALESCE(C.NAME, '') || ' ' || COALESCE(PC.NAME, '') || ' ' || COALESCE(P.NAME, '') || ' ' || COALESCE(P.DESCRIPTION, ''))) @@ TO_TSQUERY(:q)\n" +
            "     ) S\n" +
            "WHERE SCORE >= 0\n" +
            "ORDER BY SCORE DESC\n" +
            "OFFSET :o\n" +
            "LIMIT :l", nativeQuery = true)
    List<ProductEntity> searchProducts(@Param("q") String query, @Param("o") int offset, @Param("l") int limit);

    @Query("from ProductEntity where promoted=true")
    List<ProductEntity> findAllPromotedProducts();

    List<ProductEntity> findAllByWasSetupAndCatalog(boolean wasSetup, CatalogEntity catalog);

}
