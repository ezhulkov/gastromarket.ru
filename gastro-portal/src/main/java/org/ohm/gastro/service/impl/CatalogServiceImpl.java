package org.ohm.gastro.service.impl;

import com.google.common.base.Objects;
import org.apache.commons.lang.ObjectUtils;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.RatingEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.CatalogRepository;
import org.ohm.gastro.reps.CategoryRepository;
import org.ohm.gastro.reps.ProductRepository;
import org.ohm.gastro.reps.PropertyRepository;
import org.ohm.gastro.reps.PropertyValueRepository;
import org.ohm.gastro.reps.RatingRepository;
import org.ohm.gastro.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.scribe.utils.Preconditions.checkNotNull;

/**
 * Created by ezhulkov on 21.08.14.
 */
@Component("catalogService")
@Transactional
public class CatalogServiceImpl implements CatalogService {

    private final PropertyRepository propertyRepository;
    private final PropertyValueRepository propertyValueRepository;
    private final CategoryRepository categoryRepository;
    private final CatalogRepository catalogRepository;
    private final ProductRepository productRepository;
    private final RatingRepository ratingRepository;

    @Autowired
    public CatalogServiceImpl(PropertyRepository propertyRepository,
                              PropertyValueRepository propertyValueRepository,
                              CategoryRepository categoryRepository,
                              CatalogRepository catalogRepository,
                              ProductRepository productRepository,
                              RatingRepository ratingRepository) {
        this.propertyRepository = propertyRepository;
        this.propertyValueRepository = propertyValueRepository;
        this.categoryRepository = categoryRepository;
        this.catalogRepository = catalogRepository;
        this.productRepository = productRepository;
        this.ratingRepository = ratingRepository;
    }

    @Override
    public List<CategoryEntity> findAllCategories() {
        return categoryRepository.findAll(new Sort(Direction.DESC, "name"));
    }

    @Override
    public List<CategoryEntity> findAllRootCategories() {
        return categoryRepository.findAllRootCategories();
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
    public CategoryEntity saveCategory(CategoryEntity category) {
        return categoryRepository.save(category);
    }

    @Override
    public PropertyEntity saveProperty(PropertyEntity property) {
        return propertyRepository.save(property);
    }

    @Override
    public PropertyValueEntity savePropertyValue(PropertyValueEntity value) {
        return propertyValueRepository.save(value);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.delete(id);
    }

    @Override
    public void deleteProperty(Long id) {
        propertyRepository.delete(id);
    }

    @Override
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
    public void deleteCatalog(Long id) {
        catalogRepository.delete(id);
    }

    @Override
    public void saveCatalog(CatalogEntity catalog) {
        catalogRepository.save(catalog);
    }

    @Override
    public CatalogEntity findCatalog(Long id) {
        return catalogRepository.findOne(id);
    }

    @Override
    public List<CategoryEntity> findAllRootCategories(CatalogEntity catalog) {
        List<ProductEntity> allByCategoryAndCatalog = productRepository.findAllByCategoryAndCatalog(null, catalog, false);
        return allByCategoryAndCatalog.stream()
                .map(t -> t.getCategory().getParent() != null ? t.getCategory().getParent() : t.getCategory())
                .distinct()
                .sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<RatingEntity> findAllRatings(CatalogEntity catalog) {
        return ratingRepository.findAllByCatalogOrderByIdDesc(catalog);
    }

    @Override
    public void saveRating(RatingEntity rating) {
        final CatalogEntity catalog = rating.getCatalog();
        final Integer catalogRating = (Integer) ObjectUtils.defaultIfNull(catalog.getRating(), 0);
        catalog.setRating(catalogRating + rating.getRating());
        catalogRepository.save(catalog);
        ratingRepository.save(rating);
    }

    @Override
    public CatalogEntity processUploadedImages(String objectId, Map<ImageSize, String> imageUrls) {

        checkNotNull(objectId, "ObjectId should not be null");
        CatalogEntity catalog = catalogRepository.findOne(Long.parseLong(objectId));
        checkNotNull(catalog, "Product should not be null");

        catalog.setAvatarUrlSmall(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE1), catalog.getAvatarUrlSmall()));
        catalog.setAvatarUrlMedium(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE2), catalog.getAvatarUrlMedium()));
        catalog.setAvatarUrl(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE3), catalog.getAvatarUrl()));

        return catalogRepository.saveAndFlush(catalog);

    }

    @Override
    public boolean test(FileType fileType) {
        return fileType == FileType.CATALOG;
    }

}
