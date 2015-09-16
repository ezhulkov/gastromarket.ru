package org.ohm.gastro.service.impl;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.javatuples.Tuple;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OfferEntity;
import org.ohm.gastro.domain.PriceModifierEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.ProductEntity.Unit;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.PropertyValueEntity.Tag;
import org.ohm.gastro.domain.PurchaseEntity;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.misc.Throwables;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.scribe.utils.Preconditions.checkNotNull;

/**
 * Created by ezhulkov on 01.02.15.
 */
@SuppressWarnings("unchecked")
@Component
@Transactional
@ImageUploader(FileType.PRODUCT)
public class ProductServiceImpl implements ProductService, Logging {

    private final ProductRepository productRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyValueRepository propertyValueRepository;
    private final TagRepository tagRepository;
    private final PropertyService propertyService;
    private final ImageService imageService;
    private final PriceModifierRepository priceModifierRepository;

    @Autowired
    public ProductServiceImpl(final ProductRepository productRepository,
                              final PropertyRepository propertyRepository,
                              final PropertyValueRepository propertyValueRepository,
                              final TagRepository tagRepository,
                              final PropertyService propertyService,
                              final ImageService imageService,
                              final PriceModifierRepository priceModifierRepository) {
        this.productRepository = productRepository;
        this.propertyRepository = propertyRepository;
        this.propertyValueRepository = propertyValueRepository;
        this.tagRepository = tagRepository;
        this.propertyService = propertyService;
        this.imageService = imageService;
        this.priceModifierRepository = priceModifierRepository;
    }

    @Override
    public ProductEntity processUploadedImages(String objectId, Map<ImageSize, String> imageUrls) {

        checkNotNull(objectId, "ObjectId should not be null");
        ProductEntity product = productRepository.findOne(Long.parseLong(objectId));
        checkNotNull(product, "Product should not be null");

        product.setAvatarUrlSmall(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE1), product.getAvatarUrlSmall()));
        product.setAvatarUrlMedium(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE2), product.getAvatarUrlMedium()));
        product.setAvatarUrl(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE3), product.getAvatarUrl()));

        return productRepository.saveAndFlush(product);

    }

    @Override
    public List<ProductEntity> searchProducts(String query, final int from, final int to) {
        if (StringUtils.isEmpty(query)) return Collections.emptyList();
        query = query.replaceAll("\\s", "|");
        return productRepository.searchProducts(query.toLowerCase(), from, to);
    }

    @Override
    public ProductEntity saveProduct(ProductEntity product) {
        String description = product.getDescription();
        if (description != null) {
            description = description.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
            product.setDescription(description);
        }
        product.setWasSetup(true);
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
                    final Long parentValueId = getPropertyValueId(parentValueIdStr, (String) t.getValue(0));
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
        return product;
    }

    private Long getPropertyValueId(final String valueId, final String propId) {
        final String[] split = valueId.split("-");
        if (split.length > 1 && split[0].equals("new")) {
            final PropertyValueEntity newPropValue = new PropertyValueEntity();
            final String value = Arrays.stream(Arrays.copyOfRange(split, 1, split.length)).collect(Collectors.joining("-"));
            newPropValue.setName(StringUtils.capitalize(value));
            newPropValue.setClientGenerated(true);
            newPropValue.setRootValue(true);
            newPropValue.setProperty(propertyRepository.findOne(Long.parseLong(propId)));
            return propertyValueRepository.save(newPropValue).getId();
        } else {
            return Long.parseLong(valueId);
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
        return productRepository.findAllPromotedProducts();
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
    public List<ProductEntity> findAllRawProducts(@Nonnull CatalogEntity catalog) {
        return productRepository.findAllByWasSetupAndCatalog(false, catalog);
    }

    @Override
    public List<ProductEntity> findAllProducts(final OfferEntity offer) {
        return productRepository.findAllByOffer(offer);
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
    public List<ProductEntity> findProductsForFrontend(PropertyValueEntity propertyValue, CatalogEntity catalog, Boolean wasSetup,
                                                       OrderType orderType, Direction direction, String positionType, int from, int to) {
        final int count = to - from;
        if (count == 0) return Lists.newArrayList();
        final int page = from / count;
        final Sort sort = orderType == OrderType.POSITION || orderType == OrderType.NONE || orderType == null ?
                null :
                new Sort(direction, orderType.name().toLowerCase());
        final List<ProductEntity> products = findProductsInternal(propertyValue, catalog, wasSetup, new PageRequest(page, count, sort));
        return orderType == OrderType.POSITION ?
                products.stream().sorted(((o1, o2) -> ObjectUtils.compare(o1.getPositionOfType(positionType), o2.getPositionOfType(positionType)))).collect(Collectors.toList()) :
                products;
    }

    @Override
    public int findProductsForFrontendCount(final CatalogEntity catalog) {
        return productRepository.findCountCatalog(catalog);
    }

    private List<ProductEntity> findProductsInternal(PropertyValueEntity value, CatalogEntity catalog, Boolean wasSetup, Pageable page) {
        return productRepository.findAllByRootValueAndCatalog(value, catalog, wasSetup, page).getContent();
    }

    @Override
    public List<ProductEntity> findRecommendedProducts(final Long pid, final int count) {
        final ProductEntity product = productRepository.findOne(pid);
        final List<ProductEntity> products = product.getValues().stream()
                .filter(t -> t.getValue() != null)
                .flatMap(t -> t.getValue().isRootValue() ? Stream.of(t.getValue()) : t.getValue().getParents().stream()).distinct()
                .flatMap(t -> productRepository.findAllByRootValueAndCatalog(t, null, true, new PageRequest(0, 50)).getContent().stream()).distinct()
                .filter(t -> !t.equals(product))
                .collect(Collectors.toList());
        Collections.shuffle(products);
        return products.stream().limit(count).collect(Collectors.toList());
    }

    @Override
    public void promoteProduct(Long pid) {
        final ProductEntity product = productRepository.findOne(pid);
        product.setPromoted(!product.getPromoted());
        productRepository.saveAndFlush(product);
    }

    @Override
    public void importProducts(@Nonnull final Map<String, Set<MediaElement>> cachedElements, @Nonnull final CatalogEntity catalog) {
        cachedElements.entrySet().stream().flatMap(t -> t.getValue().stream()).filter(MediaElement::isChecked).forEach(element -> {
            Throwables.propagate(() -> {
                logger.info("Importing {} product", element);
                final ProductEntity product = new ProductEntity();
                product.setCatalog(catalog);
                product.setName(element.getCaption());
                product.setUnit(Unit.PIECE);
                product.setUnitValue(1);
                product.setPrice(0);
                product.setWasSetup(false);
                productRepository.save(product);
                final File file = File.createTempFile("gastromarket", "import");
                FileUtils.copyURLToFile(new URL(element.getAvatarUrl()), file);
                imageService.resizeImagePack(file,
                                             FileType.PRODUCT,
                                             product.getId().toString());
            });
        });
    }

    @Override
    public List<PriceModifierEntity> findAllModifiers(PurchaseEntity object) {
        return priceModifierRepository.findAllByEntity(object);
    }

    @Override
    public List<PropertyValueEntity> findAllRootValues(CatalogEntity catalog, Boolean wasSetup) {
        return findProductsForFrontend(null, catalog, wasSetup, null, null, null, 0, Integer.MAX_VALUE).stream()
                .flatMap(t -> t.getValues().stream())
                .map(TagEntity::getValue)
                .filter(java.util.Objects::nonNull)
                .filter(t -> t.getTag() == Tag.ROOT)
                .collect(Collectors.toList());
    }

}
