package org.ohm.gastro.reps;

import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface TagRepository extends JpaRepository<TagEntity, Long> {

    @Modifying
    @Query("delete from TagEntity where product = :product")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    void deleteAllValues(@Param("product") ProductEntity product);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<TagEntity> findAllByProduct(ProductEntity product);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<TagEntity> findAllByProductAndProperty(ProductEntity product, PropertyEntity property);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<TagEntity> findAllByProductAndValue(ProductEntity product, PropertyValueEntity value);

}
