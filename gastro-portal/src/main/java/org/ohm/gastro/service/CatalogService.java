package org.ohm.gastro.service;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.RatingEntity;
import org.ohm.gastro.domain.UserEntity;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface CatalogService extends ImageUploaderService<CatalogEntity> {

    List<CategoryEntity> findAllCategories();

    List<CategoryEntity> findAllRootCategories();

    List<PropertyEntity> findAllProperties();

    List<PropertyValueEntity> findAllValues(PropertyEntity property);

    CategoryEntity findCategory(Long id);

    PropertyEntity findProperty(Long id);

    PropertyValueEntity findPropertyValue(Long id);

    CategoryEntity saveCategory(CategoryEntity category);

    PropertyEntity saveProperty(PropertyEntity property);

    PropertyValueEntity savePropertyValue(PropertyValueEntity value);

    void deleteCategory(Long id);

    void deleteProperty(Long id);

    void deletePropertyValue(Long id);

    List<PropertyEntity> findAllProperties(CategoryEntity object);

    List<CatalogEntity> findAllCatalogs();

    List<CatalogEntity> findAllCatalogs(UserEntity user);

    void deleteCatalog(Long id);

    void saveCatalog(CatalogEntity catalog);

    CatalogEntity findCatalog(Long id);

    List<CategoryEntity> findAllRootCategories(CatalogEntity catalog);

    List<RatingEntity> findAllRatings(CatalogEntity user);

    void saveRating(RatingEntity rating);

}
