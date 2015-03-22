package org.ohm.gastro.service;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.RatingEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.CatalogRepository;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface CatalogService extends ImageUploaderService<CatalogEntity>, AltIdService<CatalogEntity, CatalogRepository> {

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

    void setupCatalog(CatalogEntity catalog);

    CatalogEntity findCatalog(Long id);

    CatalogEntity findCatalog(String altId);

    List<CategoryEntity> findAllRootCategories(CatalogEntity catalog);

    List<RatingEntity> findAllRatings(CatalogEntity user);

    void rateCatalog(final CatalogEntity catalog, final String comment, final int rating, final UserEntity user);

}
