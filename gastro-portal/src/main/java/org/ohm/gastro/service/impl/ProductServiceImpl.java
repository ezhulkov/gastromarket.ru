package org.ohm.gastro.service.impl;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.ProductEntity.Unit;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.misc.Throwables;
import org.ohm.gastro.reps.ProductRepository;
import org.ohm.gastro.reps.TagRepository;
import org.ohm.gastro.service.CatalogService;
import org.ohm.gastro.service.ImageService;
import org.ohm.gastro.service.ImageService.FileType;
import org.ohm.gastro.service.ImageService.ImageSize;
import org.ohm.gastro.service.ImageUploader;
import org.ohm.gastro.service.ProductService;
import org.ohm.gastro.service.RatingModifier;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.scribe.utils.Preconditions.checkNotNull;

/**
 * Created by ezhulkov on 01.02.15.
 */
@Component
@Transactional
@ImageUploader(FileType.PRODUCT)
public class ProductServiceImpl implements ProductService, Logging {

    private final ProductRepository productRepository;
    private final TagRepository tagRepository;
    private final CatalogService catalogService;
    private final ImageService imageService;

    @Autowired
    public ProductServiceImpl(final ProductRepository productRepository,
                              final TagRepository tagRepository,
                              final CatalogService catalogService,
                              final ImageService imageService) {
        this.productRepository = productRepository;
        this.tagRepository = tagRepository;
        this.catalogService = catalogService;
        this.imageService = imageService;
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
        return saveWithAltId(product, productRepository);
    }

    @Override
    @RatingModifier
    public void deleteProduct(Long id) {
        productRepository.delete(id);
    }

    @Override
    public ProductEntity saveProduct(ProductEntity product, Map<Long, String> propValues, Map<Long, String[]> listValues) {
        saveProduct(product);
        tagRepository.deleteAllValues(product);
        final Function<Entry<Long, String>, TagEntity> tagCreator = t -> {
            TagEntity productValue = new TagEntity();
            productValue.setProduct(product);
            productValue.setData(t.getValue());
            productValue.setProperty(catalogService.findProperty(t.getKey()));
            return productValue;
        };
        propValues.entrySet().stream()
                .filter(t -> StringUtils.isNotEmpty(t.getValue()))
                .map(tagCreator)
                .forEach(tagRepository::save);
        listValues.entrySet().stream()
                .map(t -> Arrays.stream(t.getValue()).map(v -> ImmutableMap.of(t.getKey(), v)).collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .map(t -> Iterables.getFirst(t.entrySet(), null))
                .filter(t -> StringUtils.isNotEmpty(t.getValue()))
                .map(tagCreator)
                .forEach(tagRepository::save);
        return product;
    }

    @Override
    @RatingModifier
    public ProductEntity createProduct(final ProductEntity product) {
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
    public List<ProductEntity> findAllProducts(CategoryEntity category, CatalogEntity catalog) {
        return findProductsInternal(category, catalog, null, null);
    }

    @Override
    public List<ProductEntity> findProductsForFrontend(CategoryEntity category, CatalogEntity catalog, OrderType orderType, Direction direction, int from, int to) {
        final int count = to - from;
        if (count == 0) return Lists.newArrayList();
        final int page = from / count;
        final Sort sort = orderType == OrderType.NONE || orderType == null ? null : new Sort(direction, orderType.name().toLowerCase());
        return findProductsInternal(category, catalog, true, new PageRequest(page, count, sort));
    }

    @Override
    public int findProductsForFrontendCount(final CatalogEntity catalog) {
        return productRepository.findCountCatalog(catalog);
    }

    private List<ProductEntity> findProductsInternal(CategoryEntity category, CatalogEntity catalog, Boolean wasSetup, Pageable page) {
        if (category != null && category.getChildren().size() > 0) {
            return productRepository.findAllByParentCategory(category, wasSetup, page).getContent();
        }
        return productRepository.findAllByCategoryAndCatalog(category, catalog, wasSetup, page).getContent();
    }

    @Override
    public List<ProductEntity> findRecommendedProducts(final Long pid, final int count) {
        final ProductEntity product = productRepository.findOne(pid);
        final CategoryEntity category = product.getCategory();
        if (category == null) return Lists.newArrayList();
        final CategoryEntity parentCategory = category.getParent() == null ? category : category.getParent();
        return productRepository.findAllByParentCategory(parentCategory, false, null).getContent().stream()
                .filter(p -> !p.equals(product))
                .limit(count).collect(Collectors.toList());
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
                ProductEntity product = new ProductEntity();
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

}
