package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.Region;
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
            "   (pr.wasSetup=true or :wasSetup is null) and " +
            "   (c.region=:region or :region is null)")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Page<ProductEntity> findAllByRootValueAndCatalog(@Param("value") PropertyValueEntity value,
                                                     @Param("catalog") CatalogEntity catalog,
                                                     @Param("wasSetup") Boolean wasSetup,
                                                     @Param("hidden") Boolean hidden,
                                                     @Param("region") Region region,
                                                     Pageable page);

    @Query("select distinct pr.id from ProductEntity pr " +
            "   join pr.catalog c " +
            "   left join pr.values tags " +
            "   left join tags.value v1 " +
            "   left join v1.parents v2 " +
            "where (v1=:value or v2=:value or :value is null) and " +
            "   (c.region=:region or :region is null) and " +
            "   pr.hidden=false and " +
            "   pr.wasSetup=true")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Long> findAllIdsByValue(@Param("value") PropertyValueEntity value, @Param("region") Region region);

    @Query("select count(distinct pr) from ProductEntity pr " +
            "   join pr.catalog c " +
            "   left join pr.values tags " +
            "   left join tags.value v1 " +
            "   left join v1.parents v2 " +
            "where (v1=:value or v2=:value or :value is null) and " +
            "   (pr.catalog=:catalog or :catalog is null) and " +
            "   (pr.hidden=:hidden or :hidden is null) and " +
            "   (pr.wasSetup=true or :wasSetup is null) and " +
            "   (c.region=:region or :region is null)")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    int findCountByRootValueAndCatalog(@Param("value") PropertyValueEntity value,
                                       @Param("catalog") CatalogEntity catalog,
                                       @Param("wasSetup") Boolean wasSetup,
                                       @Param("region") Region region,
                                       @Param("hidden") Boolean hidden);

    @Query("select count(distinct pr) from ProductEntity pr " +
            "   join pr.catalog c " +
            "where c=:catalog and pr.hidden=false and " +
            "   (pr.wasSetup=:wasSetup or :wasSetup is null)")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    int findCountInCatalog(@Param("catalog") CatalogEntity catalog, @Param("wasSetup") Boolean wasSetup);

    @Query("select p from ProductEntity p " +
            "   join p.catalog c " +
            "where p.promoted=true and " +
            "   (c.region=:region or :region is null)")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<ProductEntity> findAllPromotedProducts(@Param("region") Region region);

    @Query("select pr from ProductEntity pr " +
            "join pr.catalog c " +
            "where pr.wasSetup=:wasSetup and " +
            "   c=:catalog and " +
            "   (c.region=:region or :region is null) " +
            "order by pr.id desc")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<ProductEntity> findAllByWasSetupAndCatalog(@Param("wasSetup") boolean wasSetup,
                                                    @Param("catalog") CatalogEntity catalog,
                                                    @Param("region") Region region,
                                                    Pageable page);

    @Query(value = "select  distinct * \n" +
            "from (\n" +
            "       select\n" +
            "         p.*,\n" +
            "         ts_rank_cd(to_tsvector('ru', coalesce(v1.name, '') || ' ' ||\n" +
            "                                      coalesce(p.name, '') || ' ' ||\n" +
            "                                      coalesce(p.description, '')),\n" +
            "                    to_tsquery(:q)) as score\n" +
            "       from product p\n" +
            "         join catalog c on p.catalog_id = c.id\n" +
            "         left join tags tag on tag.product_id = p.id\n" +
            "         left join property_value v1 on v1.id = tag.value_id\n" +
            "       where lower(coalesce(v1.name, '') || ' ' ||\n" +
            "                   coalesce(p.name, '') || ' ' ||\n" +
            "                   coalesce(p.description, '')) @@\n" +
            "             to_tsquery(:q) and c.region=:region\n" +
            "     ) s\n" +
            "where score >= 0 and was_setup=true and hidden=false \n" +
            "order by score desc\n" +
            "offset :o\n" +
            "limit :l", nativeQuery = true)
    List<ProductEntity> searchProducts(@Param("region") String region, @Param("q") String query, @Param("o") int offset, @Param("l") int limit);

    @Query("from ProductEntity where was_checked=:checked and was_setup=true and (catalog=:catalog or :catalog is null) order by date desc")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<ProductEntity> findAllUncheckedProducts(@Param("catalog") CatalogEntity catalog, @Param("checked") Boolean wasChecked);

    @Query("from ProductEntity where import_source_url=:link")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<ProductEntity> findByImportSourceUrl(@Param("link") String link);

}
