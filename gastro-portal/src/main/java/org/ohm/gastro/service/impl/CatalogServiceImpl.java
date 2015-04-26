package org.ohm.gastro.service.impl;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.LogEntity;
import org.ohm.gastro.domain.LogEntity.Type;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.CatalogRepository;
import org.ohm.gastro.reps.CategoryRepository;
import org.ohm.gastro.reps.CommentRepository;
import org.ohm.gastro.reps.LogRepository;
import org.ohm.gastro.reps.ProductRepository;
import org.ohm.gastro.reps.PropertyRepository;
import org.ohm.gastro.reps.PropertyValueRepository;
import org.ohm.gastro.service.CatalogService;
import org.ohm.gastro.service.ImageService.FileType;
import org.ohm.gastro.service.ImageService.ImageSize;
import org.ohm.gastro.service.ImageUploader;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.scribe.utils.Preconditions.checkNotNull;

/**
 * Created by ezhulkov on 21.08.14.
 */
@Component("catalogService")
@Transactional
@ImageUploader(FileType.CATALOG)
public class CatalogServiceImpl implements CatalogService, Logging {

    private final PropertyRepository propertyRepository;
    private final PropertyValueRepository propertyValueRepository;
    private final CategoryRepository categoryRepository;
    private final CatalogRepository catalogRepository;
    private final ProductRepository productRepository;
    private final CommentRepository commentRepository;
    private final LogRepository logRepository;

    private final int historyDays;
    private final float retentionCoeff;
    private final float posRatingCoeff;
    private final float negRatingCoeff;
    private final float transactionCoeff;
    private final float productionCoeff;
    private final float productsCoeff;

    @Autowired
    public CatalogServiceImpl(PropertyRepository propertyRepository,
                              PropertyValueRepository propertyValueRepository,
                              CategoryRepository categoryRepository,
                              CatalogRepository catalogRepository,
                              ProductRepository productRepository,
                              CommentRepository commentRepository,
                              LogRepository logRepository,
                              @Value("rating.history.days") int historyDays,
                              @Value("rating.retention.coeff") float retentionCoeff,
                              @Value("rating.pos.rating.coeff") float posRatingCoeff,
                              @Value("rating.neg.rating.coeff") float negRatingCoeff,
                              @Value("rating.transaction.coeff") float transactionCoeff,
                              @Value("rating.production.coeff") float productionCoeff,
                              @Value("rating.products.coeff") float productsCoeff) {
        this.propertyRepository = propertyRepository;
        this.propertyValueRepository = propertyValueRepository;
        this.categoryRepository = categoryRepository;
        this.catalogRepository = catalogRepository;
        this.productRepository = productRepository;
        this.commentRepository = commentRepository;
        this.logRepository = logRepository;
        this.historyDays = historyDays;
        this.retentionCoeff = retentionCoeff;
        this.posRatingCoeff = posRatingCoeff;
        this.negRatingCoeff = negRatingCoeff;
        this.transactionCoeff = transactionCoeff;
        this.productionCoeff = productionCoeff;
        this.productsCoeff = productsCoeff;
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
        return propertyRepository.findAll(new Sort(Direction.ASC, "name"));
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
    public List<CatalogEntity> findAllActiveCatalogs() {
        return catalogRepository.findAllActive();
    }

    @Override
    public List<CatalogEntity> findAllCatalogs(UserEntity user) {
        if (user == null) return Lists.newArrayList();
        return catalogRepository.findAllByUser(user);
    }

    @Override
    public void deleteCatalog(Long id) {
        catalogRepository.delete(id);
    }

    @Override
    public void saveCatalog(CatalogEntity catalog) {
        saveWithAltId(catalog, catalogRepository);
    }

    @Override
    public void setupCatalog(final CatalogEntity catalog) {
        catalog.setWasSetup(true);
        catalogRepository.save(catalog);
    }

    @Override
    public CatalogEntity findCatalog(Long id) {
        return catalogRepository.findOne(id);
    }

    @Override
    public CatalogEntity findCatalog(final String altId) {
        return findByAltId(altId, catalogRepository);
    }

    @Override
    public List<CategoryEntity> findAllRootCategories(CatalogEntity catalog) {
        List<ProductEntity> allByCategoryAndCatalog = productRepository.findAllByCategoryAndCatalog(null, catalog, false, null).getContent();
        return allByCategoryAndCatalog.stream()
                .map(t -> t.getCategory().getParent() != null ? t.getCategory().getParent() : t.getCategory())
                .distinct()
                .sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentEntity> findAllComments(CatalogEntity catalog) {
        return commentRepository.findAllByCatalogOrderByIdDesc(catalog);
    }

    @Override
    public void rateCatalog(final CatalogEntity catalog, final String text, final int rating, final UserEntity user) {

        if (StringUtils.isEmpty(text) || user == null) return;
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setCatalog(catalog);
        commentEntity.setAuthor(user);
        commentEntity.setText(text);
        commentEntity.setDate(new Timestamp(System.currentTimeMillis()));
        commentEntity.setRating(rating);
        commentRepository.save(commentEntity);

    }

    @Override
    public void updateRating(final CatalogEntity catalog) {

        final Date fromDate = DateUtils.addDays(new Date(), -historyDays);
        final List<CommentEntity> ratings = commentRepository.findAllRatings(catalog);
        final List<LogEntity> catalogOps = logRepository.findAll(catalog.getUser(), catalog, fromDate);

        final int productsCount = catalog.getReadyProducts().size();
        final int retentionCount = logRepository.findAll(catalog.getUser(), fromDate, Type.LOGIN).size();
        final int posCount = (int) ratings.stream().filter(t -> t.getRating() > 0).count();
        final int negCount = (int) ratings.stream().filter(t -> t.getRating() < 0).count();
        final int totalSum = (int) catalogOps.stream().filter(t -> t.getType() == Type.ORDER_DONE).mapToLong(LogEntity::getCount).sum();
        final int doneCount = (int) catalogOps.stream().filter(t -> t.getType() == Type.ORDER_DONE).count();
        final int cancelledCount = (int) catalogOps.stream().filter(t -> t.getType() == Type.ORDER_CANCELLED).count();

        logger.info("Updating rating for {}", catalog);

        catalog.setRating(calcRating(productsCount, retentionCount, posCount, negCount, doneCount, cancelledCount, totalSum));

        catalogRepository.save(catalog);

    }

    private int calcRating(final int productsCount, final int retentionCount, final int posCount, final int negCount, final int doneCount, final int cancelledCount, final int totalSum) {
        return (int) (
                productsCount * productsCoeff +
                        retentionCount * retentionCoeff +
                        posCount * posRatingCoeff +
                        negCount * negRatingCoeff +
                        doneCount / cancelledCount * productionCoeff +
                        totalSum * transactionCoeff
        );
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

}
