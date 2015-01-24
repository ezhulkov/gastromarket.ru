package org.ohm.gastro.service;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.RatingEntity;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.domain.UserEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface CatalogService extends ImageUploaderService<ProductEntity> {

    public List<ProductEntity> searchProducts(String query);

    public List<CategoryEntity> findAllCategories();

    public List<CategoryEntity> findAllRootCategories();

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

    public List<CatalogEntity> findNotSetupCatalogs(UserEntity user);

    void deleteCatalog(Long id);

    void saveCatalog(CatalogEntity catalog);

    CatalogEntity findCatalog(Long id);

    ProductEntity saveProduct(ProductEntity product);

    void deleteProduct(Long id);

    ProductEntity saveProduct(ProductEntity product, Map<Long, String> propValues, Map<Long, String[]> listValues);

    List<TagEntity> findAllTags(ProductEntity oneProduct);

    List<TagEntity> findAllTags(ProductEntity product, PropertyEntity property);

    List<ProductEntity> findAllProducts();

    List<ProductEntity> findPromotedProducts();

    ProductEntity findProduct(Long id);

    List<ProductEntity> findAllProducts(CategoryEntity category, CatalogEntity catalog);

    List<CategoryEntity> findAllRootCategories(CatalogEntity catalog);

    List<RatingEntity> findAllRatings(CatalogEntity user);

    void saveRating(RatingEntity rating);

    void showProduct(Long pid);

}
