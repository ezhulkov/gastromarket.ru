package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    @Query("from CategoryEntity where parent is null order by name desc")
    List<CategoryEntity> findAllRootCategories();

}
