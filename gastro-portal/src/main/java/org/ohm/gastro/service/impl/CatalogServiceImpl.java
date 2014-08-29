package org.ohm.gastro.service.impl;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.CatalogRepository;
import org.ohm.gastro.reps.CategoryRepository;
import org.ohm.gastro.reps.ProductRepository;
import org.ohm.gastro.reps.ProductValueRepository;
import org.ohm.gastro.reps.PropertyRepository;
import org.ohm.gastro.reps.PropertyValueRepository;
import org.ohm.gastro.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
@Component("catalogService")
public class CatalogServiceImpl implements CatalogService {

    private final PropertyRepository propertyRepository;
    private final PropertyValueRepository propertyValueRepository;
    private final CategoryRepository categoryRepository;
    private final CatalogRepository catalogRepository;
    private final ProductRepository productRepository;
    private final ProductValueRepository productValueRepository;

    @Autowired
    public CatalogServiceImpl(PropertyRepository propertyRepository,
                              PropertyValueRepository propertyValueRepository,
                              CategoryRepository categoryRepository,
                              CatalogRepository catalogRepository,
                              ProductRepository productRepository,
                              ProductValueRepository productValueRepository) {
        this.propertyRepository = propertyRepository;
        this.propertyValueRepository = propertyValueRepository;
        this.categoryRepository = categoryRepository;
        this.catalogRepository = catalogRepository;
        this.productRepository = productRepository;
        this.productValueRepository = productValueRepository;
    }

    @Override
    public List<CategoryEntity> findAllCategories() {
        return categoryRepository.findAll(new Sort(Direction.DESC, "name"));
    }

    @Override
    public List<PropertyEntity> findAllProperties() {
        return propertyRepository.findAll(new Sort(Direction.DESC, "name"));
    }

    @Override
    public List<PropertyValueEntity> findAllValues(PropertyEntity property) {
        return propertyValueRepository.findAllByProperty(property, new Sort(Direction.DESC, "value"));
    }

    @Override
    public CategoryEntity findCategory(Long id) {
        return categoryRepository.findOne(id);
    }

    @Override
    public PropertyEntity findProperty(Long id) {
        return propertyRepository.findOne(id);
    }

    @Override
    public PropertyValueEntity findPropertyValue(Long id) {
        return propertyValueRepository.findOne(id);
    }

    @Override
    @Transactional
    public CategoryEntity saveCategory(CategoryEntity category) {
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public PropertyEntity saveProperty(PropertyEntity property) {
        return propertyRepository.save(property);
    }

    @Override
    @Transactional
    public PropertyValueEntity savePropertyValue(PropertyValueEntity value) {
        return propertyValueRepository.save(value);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.delete(id);
    }

    @Override
    @Transactional
    public void deleteProperty(Long id) {
        propertyRepository.delete(id);
    }

    @Override
    @Transactional
    public void deletePropertyValue(Long id) {
        propertyValueRepository.delete(id);
    }

    @Override
    public List<PropertyEntity> findAllProperties(CategoryEntity category) {
        return propertyRepository.findAllProperties(category);
    }

    @Override
    public List<CatalogEntity> findAllCatalogs() {
        return catalogRepository.findAll();
    }

    @Override
    public List<CatalogEntity> findAllCatalogs(UserEntity user) {
        return catalogRepository.findAllByUser(user);
    }

    @Override
    @Transactional
    public void deleteCatalog(Long id) {
        catalogRepository.delete(id);
    }

    @Override
    @Transactional
    public void saveCatalog(CatalogEntity catalog) {
        catalogRepository.save(catalog);
    }

    @Override
    public CatalogEntity findCatalog(Long id) {
        return catalogRepository.findOne(id);
    }

    @Override
    @Transactional
    public void saveProduct(ProductEntity product) {
        productRepository.save(product);
    }

    @Override
    public List<ProductEntity> findAllProducts(CatalogEntity catalog) {
        return productRepository.findAllByCatalog(catalog);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.delete(id);
    }

}
