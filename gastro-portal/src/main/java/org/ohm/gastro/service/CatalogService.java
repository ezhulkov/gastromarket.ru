package org.ohm.gastro.service;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.UserEntity;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface CatalogService {

    public List<CategoryEntity> findAllCategories();

    public List<PropertyEntity> findAllProperties();

    public List<PropertyValueEntity> findAllValues(PropertyEntity property);

    public CategoryEntity findCategory(Long id);

    public PropertyEntity findProperty(Long id);

    public PropertyValueEntity findPropertyValue(Long id);

    public CategoryEntity saveCategory(CategoryEntity category);

    public PropertyEntity saveProperty(PropertyEntity property);

    public PropertyValueEntity savePropertyValue(PropertyValueEntity value);

    public void deleteCategory(Long id);

    public void deleteProperty(Long id);

    public void deletePropertyValue(Long id);

    public List<PropertyEntity> findAllProperties(CategoryEntity object);

    public List<CatalogEntity> findAllCatalogs();

    public List<CatalogEntity> findAllCatalogs(UserEntity user);

    void deleteCatalog(Long id);

    void saveCatalog(CatalogEntity catalog);

    CatalogEntity findCatalog(Long id);

    void saveProduct(ProductEntity product);

    List<ProductEntity> findAllProducts(CatalogEntity object);

    void deleteProduct(Long id);

}
