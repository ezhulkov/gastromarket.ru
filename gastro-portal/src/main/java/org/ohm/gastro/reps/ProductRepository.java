package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface ProductRepository extends AltIdRepository<ProductEntity> {

    @Query("select distinct pr from ProductEntity pr " +
            "   join pr.catalog c " +
            "   left join pr.values pv " +
            "   left join pv.value v1 " +
            "   left join v1.parents v2 " +
            "where (v1=:value or v2=:value or :value is null) and " +
            "   (pr.catalog=:catalog or :catalog is null) and " +
            "   (pr.wasSetup=true or :wasSetup is null)")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Page<ProductEntity> findAllByRootValueAndCatalog(@Param("value") PropertyValueEntity value, @Param("catalog") CatalogEntity catalog, @Param("wasSetup") Boolean wasSetup, Pageable page);

    @Query("select count(*) from ProductEntity where catalog=:catalog and wasSetup=false")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    int findCountCatalog(@Param("catalog") CatalogEntity catalog);

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
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<ProductEntity> findAllPromotedProducts();

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<ProductEntity> findAllByWasSetupAndCatalog(boolean wasSetup, CatalogEntity catalog);

}
