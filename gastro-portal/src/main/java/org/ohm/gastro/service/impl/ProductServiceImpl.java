package org.ohm.gastro.service.impl;

import com.google.common.base.Objects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Tuple;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.PriceModifierEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.ProductEntity.Unit;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.PropertyValueEntity.Tag;
import org.ohm.gastro.domain.PurchaseEntity;
import org.ohm.gastro.domain.Region;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.filter.RegionFilter;
import org.ohm.gastro.reps.PriceModifierRepository;
import org.ohm.gastro.reps.ProductRepository;
import org.ohm.gastro.reps.PropertyRepository;
import org.ohm.gastro.reps.PropertyValueRepository;
import org.ohm.gastro.reps.TagRepository;
import org.ohm.gastro.service.ImageService;
import org.ohm.gastro.service.ImageService.FileType;
import org.ohm.gastro.service.ImageService.ImageSize;
import org.ohm.gastro.service.ImageUploader;
import org.ohm.gastro.service.ProductService;
import org.ohm.gastro.service.PropertyService;
import org.ohm.gastro.service.RatingModifier;
import org.ohm.gastro.service.RatingTarget;
import org.ohm.gastro.service.social.MediaElement;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.scribe.utils.Preconditions.checkNotNull;

/**
 * Created by ezhulkov on 01.02.15.
 */
@SuppressWarnings("unchecked")
@Component("productService")
@Transactional
@ImageUploader(FileType.PRODUCT)
public class ProductServiceImpl implements ProductService, Logging {

    private final static String SPHINX_SEARCH = "select id from gastro_index where match('%s') and region='%s' limit 100";

    private final DataSource sphinxSource;
    private final ProductRepository productRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyValueRepository propertyValueRepository;
    private final TagRepository tagRepository;
    private final PropertyService propertyService;
    private final ImageService imageService;
    private final PriceModifierRepository priceModifierRepository;
    private final LoadingCache<CatalogCategoryPair, Integer> cachedCategoriesProducts = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(new CacheLoader<CatalogCategoryPair, Integer>() {
                @Override
                public Integer load(final CatalogCategoryPair cat) throws Exception {
                    return findAllCategoryProductsCount(cat.catalog, cat.category, cat.region);
                }
            });

    @Autowired
    public ProductServiceImpl(@Qualifier("sphinxSource") final DataSource sphinxSource,
                              final ProductRepository productRepository,
                              final PropertyRepository propertyRepository,
                              final PropertyValueRepository propertyValueRepository,
                              final TagRepository tagRepository,
                              final PropertyService propertyService,
                              final ImageService imageService,
                              final PriceModifierRepository priceModifierRepository) {
        this.sphinxSource = sphinxSource;
        this.productRepository = productRepository;
        this.propertyRepository = propertyRepository;
        this.propertyValueRepository = propertyValueRepository;
        this.tagRepository = tagRepository;
        this.propertyService = propertyService;
        this.imageService = imageService;
        this.priceModifierRepository = priceModifierRepository;
    }

    @Override
    public void processUploadedImages(final FileType fileType, String objectId, Map<ImageSize, String> imageUrls) {
        checkNotNull(objectId, "ObjectId should not be null");
        final ProductEntity product = productRepository.findOne(Long.parseLong(objectId));
        checkNotNull(product, "Product should not be null");
        product.setAvatarUrlSmall(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE1), product.getAvatarUrlSmall()));
        product.setAvatarUrlMedium(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE2), product.getAvatarUrlMedium()));
        product.setAvatarUrl(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE3), product.getAvatarUrl()));
        product.setAvatarUrlBig(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE4), product.getAvatarUrlBig()));
        productRepository.save(product);
    }

    @Override
    public List<ProductEntity> searchProducts(String query, final int from, final int to) {
        if (StringUtils.isEmpty(query)) return Collections.emptyList();
        final List<Long> pIds = new JdbcTemplate(sphinxSource)
                .query(
                        String.format(SPHINX_SEARCH, query, RegionFilter.getCurrentRegion()),
                        (rs, rowNum) -> rs.getLong(1)
                );
        return productRepository.findAll(pIds);
//        query = Arrays.stream(query.split("\\s")).map(String::trim).filter(StringUtils::isNoneEmpty).collect(Collectors.joining("|"));
//        return productRepository.searchProducts(RegionFilter.getCurrentRegion().name(), query.toLowerCase(), from, to);
    }

    @Override
    public ProductEntity saveProduct(ProductEntity product) {
        String description = product.getDescription();
        if (description != null) {
            description = description.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
            product.setDescription(description);
        }
        product.setLastModified(new Date());
        return saveWithAltId(product, productRepository);
    }

    @Override
    @RatingModifier
    public void deleteProduct(Long id, @RatingTarget final CatalogEntity catalog) {
        productRepository.delete(id);
    }

    @Override
    public ProductEntity saveProduct(ProductEntity product, Map<Long, String> propValues, List<Tuple> listValues) {
        product.setWasSetup(true);
        saveProduct(product);
        tagRepository.deleteAllValues(product);
        propValues.entrySet().stream()
                .map(t -> {
                    final PropertyEntity property = propertyService.findProperty(t.getKey());
                    final TagEntity tag = new TagEntity();
                    tag.setProduct(product);
                    tag.setProperty(property);
                    tag.setData(t.getValue());
                    return tag;
                })
                .forEach(tagRepository::save);
        listValues.stream()
                .forEach(t -> {
                    final String parentValueIdStr = (String) t.getValue(1);
                    getPropertyValueIds(parentValueIdStr, (String) t.getValue(0)).forEach(parentValueId -> {
                        final Long childValueId = t.getSize() == 3 ? (Long) t.getValue(2) : null;
                        final PropertyValueEntity parentValue = propertyService.findPropertyValue(parentValueId);
                        final PropertyEntity property = parentValue.getProperty();
                        final TagEntity parentTag = new TagEntity();
                        final TagEntity childTag;
                        parentTag.setProduct(product);
                        parentTag.setProperty(property);
                        parentTag.setValue(parentValue);
                        tagRepository.save(parentTag);
                        if (childValueId != null) {
                            final PropertyValueEntity childValue = propertyService.findPropertyValue(childValueId);
                            childTag = new TagEntity();
                            childTag.setProduct(product);
                            childTag.setProperty(property);
                            childTag.setValue(childValue);
                            childTag.setData(parentTag.getId().toString());
                            tagRepository.save(childTag);
                        }
                    });
                });
        return product;
    }

    @Override
    public void saveProduct(ProductEntity product, List<PropertyValueEntity> newValues) {
        final List<TagEntity> tags = newValues.stream()
                .map(t -> {
                    final PropertyEntity property = propertyService.findProperty(t.getProperty().getId());
                    final TagEntity tag = new TagEntity();
                    tag.setProduct(product);
                    tag.setProperty(property);
                    tag.setValue(t);
                    return tag;
                }).collect(Collectors.toList());
        tagRepository.save(tags);
    }

    private List<Long> getPropertyValueIds(final String valueId, final String propId) {
        final String[] split = valueId.split("-");
        if (split.length > 1 && split[0].equals("new")) {
            final String values = Arrays.stream(Arrays.copyOfRange(split, 1, split.length)).collect(Collectors.joining("-"));
            if (StringUtils.isNoneEmpty(values)) {
                return Arrays.stream(values.split(",")).filter(StringUtils::isNotEmpty).map(value -> {
                    final PropertyEntity property = propertyRepository.findOne(Long.parseLong(propId));
                    final String capitalize = StringUtils.capitalize(value.trim());
                    final List<PropertyValueEntity> existingValues = propertyValueRepository.findAllByPropertyAndName(property, capitalize.toLowerCase());
                    if (existingValues.isEmpty()) {
                        final PropertyValueEntity newPropValue = new PropertyValueEntity();
                        newPropValue.setName(capitalize);
                        newPropValue.setClientGenerated(true);
                        newPropValue.setRootValue(true);
                        newPropValue.setProperty(property);
                        return propertyValueRepository.save(newPropValue).getId();
                    } else {
                        return existingValues.get(0).getId();
                    }
                }).filter(t -> t != null).collect(Collectors.toList());
            }
            return Lists.newArrayList();
        } else {
            return Lists.newArrayList(Long.parseLong(valueId));
        }
    }

    @Override
    @RatingModifier
    public ProductEntity createProduct(final ProductEntity product, @RatingTarget final CatalogEntity catalog) {
        return saveProduct(product);
    }

    @Override
    public List<TagEntity> findAllTags(ProductEntity product) {
        return tagRepository.findAllByProduct(product);
    }

    @Override
    public List<ProductEntity> findPromotedProducts() {
        return productRepository.findAllPromotedProducts(RegionFilter.getCurrentRegion());
    }

    @Override
    public ProductEntity findProduct(Long id) {
        return productRepository.findOne(id);
    }

    @Override
    public ProductEntity findProduct(final String altId) {
        return findByAltId(altId, productRepository);
    }

    @Override
    public List<ProductEntity> findAllRawProducts(@Nonnull CatalogEntity catalog, final int from, final int to) {
        final int count = to - from;
        if (count == 0) return Lists.newArrayList();
        final int page = from / count;
        return productRepository.findAllByWasSetupAndCatalog(false, catalog, null, new PageRequest(page, count));
    }

    @Override
    public int findAllCategoryProductsCount(@Nullable CatalogEntity catalog, @Nullable PropertyValueEntity category, @Nonnull Region region) {
        return category == null || category.getId() == null ?
                productRepository.findCountInCatalog(catalog, false) :
                productRepository.findCountByRootValueAndCatalog(category, catalog, null, region, false);
    }

    @Override
    public int findAllCategoryProductsCount(@Nullable CatalogEntity catalog, @Nullable PropertyValueEntity category) {
        return findAllCategoryProductsCount(catalog, category, RegionFilter.getCurrentRegion());
    }

    @Override
    public PriceModifierEntity findPriceModifier(Long id) {
        return priceModifierRepository.findOne(id);
    }

    @Override
    public void attachPriceModifiers(final PurchaseEntity object, final List<PriceModifierEntity> submittedModifiers) {
        final List<PriceModifierEntity> existing = priceModifierRepository.findAllByEntity(object);
        priceModifierRepository.delete(CollectionUtils.subtract(existing, submittedModifiers));
        final List<PriceModifierEntity> modifiers = submittedModifiers.stream().filter(t -> t.getPrice() != null && t.getDescription() != null).map(t -> {
            t.setEntity(object);
            return t;
        }).collect(Collectors.toList());
        priceModifierRepository.save(modifiers);
    }

    @Override
    public void productPosition(final List<Long> collect, String type) {
        int pos = 1;
        for (Long id : collect) {
            final ProductEntity product = productRepository.findOne(id);
            product.setPositionOfType(type, pos++);
            productRepository.save(product);
        }
    }

    @Override
    public void hideProduct(Long pid) {
        final ProductEntity product = productRepository.findOne(pid);
        product.setHidden(!product.getHidden());
        productRepository.save(product);
    }

    @Override
    public List<ProductEntity> findProductsForFrontend(PropertyValueEntity propertyValue, CatalogEntity catalog, Boolean wasSetup,
                                                       Boolean hidden, Region region, OrderType orderType, Direction direction, String positionType, int from, int to) {
        final int count = to - from;
        if (count == 0) return Lists.newArrayList();
        final int page = from / count;
        final Sort sort = orderType == OrderType.POSITION || orderType == OrderType.NONE || orderType == null ?
                new Sort(Direction.DESC, "date") :
                new Sort(direction, orderType.name().toLowerCase());
        final List<ProductEntity> products = productRepository.findAllByRootValueAndCatalog(propertyValue,
                                                                                            catalog,
                                                                                            wasSetup,
                                                                                            hidden,
                                                                                            region,
                                                                                            new PageRequest(page, count, sort)).getContent();
        return orderType == OrderType.POSITION ?
                products.stream().sorted(((o1, o2) -> ObjectUtils.compare(o1.getPositionOfType(positionType), o2.getPositionOfType(positionType)))).collect(Collectors.toList()) :
                products;
    }

    @Override
    public int findProductsForFrontendCount(final CatalogEntity catalog) {
        return productRepository.findCountInCatalog(catalog, true);
    }

    @Override
    public List<ProductEntity> findRecommendedProducts(final Long pid, final int count) {
        final ProductEntity product = productRepository.findOne(pid);
        List<TagEntity> tags = tagRepository.findAllByProduct(product);
        final Function<Tag, List<Long>> f = tag -> tags.stream()
                .filter(t -> t != null && t.getValue() != null)
                .flatMap(t -> t.getValue().isRootValue() ? Stream.of(t.getValue()) : t.getValue().getParents().stream()).distinct()
                .filter(t -> t != null && t.getTag() == tag)
                .flatMap(t -> productRepository.findAllIdsByValue(t, RegionFilter.getCurrentRegion()).stream())
                .distinct()
                .collect(Collectors.toList());
        final List<Long> events = f.apply(Tag.EVENT);
        final List<Long> categories = f.apply(Tag.ROOT);
        final List<Long> pids = CollectionUtils.intersection(events, categories).stream().filter(t -> !t.equals(pid)).collect(Collectors.toList());
        Collections.shuffle(pids);
        return productRepository.findAll(pids.stream().limit(count).collect(Collectors.toList()));
    }

    @Override
    public List<ProductEntity> findUncheckedProducts(CatalogEntity catalog, Boolean wasChecked) {
        return productRepository.findAllUncheckedProducts(catalog, wasChecked);
    }

    @Override
    public void promoteProduct(Long pid) {
        final ProductEntity product = productRepository.findOne(pid);
        product.setPromoted(!product.getPromoted());
        productRepository.saveAndFlush(product);
    }

    @Override
    @RatingModifier
    public void importProducts(@Nonnull final Map<String, Set<MediaElement>> cachedElements, @Nonnull @RatingTarget final CatalogEntity catalog) {
        synchronized (this) {
            cachedElements.entrySet().stream().flatMap(t -> t.getValue().stream()).filter(MediaElement::isChecked).distinct().forEach(element -> {
                try {
                    if (productRepository.findByImportSourceUrl(element.getLink()).size() > 0) {
                        logger.info("Product {} already exists", element.getLink());
                        return;
                    }
                    logger.info("Importing {} product", element);
                    final ProductEntity product = new ProductEntity();
                    product.setCatalog(catalog);
                    product.setName(element.getCaption());
                    product.setUnit(Unit.PIECE);
                    product.setUnitValue(1);
                    product.setPrice(0);
                    product.setImportSourceUrl(element.getLink());
                    productRepository.save(product);
                    final File file = File.createTempFile("gastromarket", "import");
                    FileUtils.copyURLToFile(new URL(element.getAvatarUrl()), file);
                    imageService.resizeImagePack(file,
                                                 FileType.PRODUCT,
                                                 product.getId().toString(),
                                                 null, null, null, null, null, null);
                } catch (IOException e) {
                    logger.error("", e);
                }
            });
        }
    }

    @Override
    public List<PriceModifierEntity> findAllModifiers(PurchaseEntity object) {
        return priceModifierRepository.findAllByEntity(object);
    }

    @Override
    public List<PropertyValueEntity> findAllRootValues(CatalogEntity catalog, Boolean wasSetup) {
        return propertyValueRepository.findAllRootValues(catalog, wasSetup);
    }

    @Override
    public int findAllProductsCountCached(final CatalogEntity catalog, @Nonnull final PropertyValueEntity category, @Nonnull Region region) {
        try {
            return cachedCategoriesProducts.get(new CatalogCategoryPair(catalog, category, region));
        } catch (ExecutionException e) {
            logger.error("", e);
        }
        return 0;
    }

    private class CatalogCategoryPair {

        private final CatalogEntity catalog;
        private final PropertyValueEntity category;
        private final Region region;

        public CatalogCategoryPair(final CatalogEntity catalog, final PropertyValueEntity category, Region region) {
            this.catalog = catalog;
            this.category = category;
            this.region = region;
        }
    }

}
