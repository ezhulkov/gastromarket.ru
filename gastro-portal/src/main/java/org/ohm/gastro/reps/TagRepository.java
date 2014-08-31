package org.ohm.gastro.reps;

import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface TagRepository extends JpaRepository<TagEntity, Long> {

    @Modifying
    @Query("delete from TagEntity where product = :product")
    void deleteAllValues(@Param("product") ProductEntity product);

    List<TagEntity> findAllByProduct(ProductEntity product);

}
