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

public interface ProductRepository extends AltIdRepository<ProductEntity> {

    @Query("select distinct pr from ProductEntity pr " +
            "   join pr.catalog c " +
            "   left join pr.values tags " +
            "   left join tags.value v1 " +
            "   left join v1.parents v2 " +
            "where (v1=:value or v2=:value or :value is null) and " +
            "   (pr.catalog=:catalog or :catalog is null) and " +
            "   (pr.hidden=:hidden or :hidden is null) and " +
            "   (pr.wasSetup=true or :wasSetup is null)")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Page<ProductEntity> findAllByRootValueAndCatalog(@Param("value") PropertyValueEntity value,
                                                     @Param("catalog") CatalogEntity catalog,
                                                     @Param("wasSetup") Boolean wasSetup,
                                                     @Param("hidden") Boolean hidden,
                                                     Pageable page);

    @Query("select count(distinct pr) from ProductEntity pr " +
            "   join pr.catalog c " +
            "   left join pr.values tags " +
            "   left join tags.value v1 " +
            "   left join v1.parents v2 " +
            "where (v1=:value or v2=:value or :value is null) and " +
            "   (pr.catalog=:catalog or :catalog is null) and " +
            "   (pr.hidden=:hidden or :hidden is null) and " +
            "   (pr.wasSetup=true or :wasSetup is null)")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    int findCountByRootValueAndCatalog(@Param("value") PropertyValueEntity value,
                                       @Param("catalog") CatalogEntity catalog,
                                       @Param("wasSetup") Boolean wasSetup,
                                       @Param("hidden") Boolean hidden);

    @Query("select count(*) from ProductEntity where catalog=:catalog and (wasSetup=:wasSetup or :wasSetup is null)")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    int findCountInCatalog(@Param("catalog") CatalogEntity catalog, @Param("wasSetup") Boolean wasSetup);

    @Query("from ProductEntity where promoted=true")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<ProductEntity> findAllPromotedProducts();

    @Query("from ProductEntity where wasSetup=:wasSetup and catalog=:catalog order by id desc")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<ProductEntity> findAllByWasSetupAndCatalog(@Param("wasSetup") boolean wasSetup,
                                                    @Param("catalog") CatalogEntity catalog,
                                                    Pageable page);

    @Query(value = "SELECT  DISTINCT * \n" +
            "FROM (\n" +
            "       SELECT\n" +
            "         P.*,\n" +
            "         TS_RANK_CD(TO_TSVECTOR('RU', COALESCE(V1.NAME, '') || ' ' ||\n" +
            "                                      COALESCE(P.NAME, '') || ' ' ||\n" +
            "                                      COALESCE(P.DESCRIPTION, '')),\n" +
            "                    TO_TSQUERY(:q)) AS SCORE\n" +
            "       FROM PRODUCT P\n" +
            "         LEFT JOIN TAGS TAG ON TAG.PRODUCT_ID = P.ID\n" +
            "         LEFT JOIN PROPERTY_VALUE V1 ON V1.ID = TAG.VALUE_ID\n" +
            "       WHERE LOWER(COALESCE(V1.NAME, '') || ' ' ||\n" +
            "                   COALESCE(P.NAME, '') || ' ' ||\n" +
            "                   COALESCE(P.DESCRIPTION, '')) @@\n" +
            "             TO_TSQUERY(:q)\n" +
            "     ) S\n" +
            "WHERE SCORE >= 0\n" +
            "ORDER BY SCORE DESC\n" +
            "OFFSET :o\n" +
            "LIMIT :l", nativeQuery = true)
    List<ProductEntity> searchProducts(@Param("q") String query, @Param("o") int offset, @Param("l") int limit);

    @Query("from ProductEntity where was_checked=:checked and was_setup=true and (catalog=:catalog or :catalog is null)")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<ProductEntity> findAllUncheckedProducts(@Param("catalog") CatalogEntity catalog, @Param("checked") Boolean wasChecked);

    @Query("from ProductEntity where import_source_url=:link")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<ProductEntity> findByImportSourceUrl(@Param("link") String link);

}
