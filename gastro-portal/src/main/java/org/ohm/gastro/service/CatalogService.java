package org.ohm.gastro.service;

import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface CatalogService {

    public List<CategoryEntity> findAllCategories();

    public List<PropertyEntity> findAllProperties();

    public List<PropertyValueEntity> findAllValues(PropertyEntity property);

    public CategoryEntity findCategory(Long id);

    public CategoryEntity saveCategory(CategoryEntity categoryEntity);

    public void deleteCategory(Long id);

}
