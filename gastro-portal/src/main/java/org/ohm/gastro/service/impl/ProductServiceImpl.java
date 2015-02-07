package org.ohm.gastro.service.impl;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.reps.ProductRepository;
import org.ohm.gastro.reps.TagRepository;
import org.ohm.gastro.service.CatalogService;
import org.ohm.gastro.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.scribe.utils.Preconditions.checkNotNull;

/**
 * Created by ezhulkov on 01.02.15.
 */
@Component
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final TagRepository tagRepository;
    private final CatalogService catalogService;

    @Autowired
    public ProductServiceImpl(final ProductRepository productRepository,
                              final TagRepository tagRepository,
                              final CatalogService catalogService) {
        this.productRepository = productRepository;
        this.tagRepository = tagRepository;
        this.catalogService = catalogService;
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
    public List<ProductEntity> searchProducts(String query) {
        if (StringUtils.isEmpty(query)) return Collections.emptyList();
        query = query.replaceAll("\\s", "|");
        return productRepository.searchProducts(query);
    }

    @Override
    public boolean test(FileType fileType) {
        return fileType == FileType.PRODUCT;
    }

    @Override
    public ProductEntity saveProduct(ProductEntity product) {
        String description = product.getDescription();
        if (description != null) {
            description = description.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
            product.setDescription(description);
        }
        return productRepository.saveAndFlush(product);
    }

    @Override
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
    public List<TagEntity> findAllTags(ProductEntity product) {
        return tagRepository.findAllByProduct(product);
    }

    @Override
    public List<TagEntity> findAllTags(ProductEntity product, PropertyEntity property) {
        return tagRepository.findAllByProductAndProperty(product, property);
    }

    @Override
    public List<ProductEntity> findAllProducts() {
        return productRepository.findAll();
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
    public List<ProductEntity> findAllProducts(CategoryEntity category, CatalogEntity catalog, Boolean hidden) {
        if (category != null && category.getChildren().size() > 0) {
            return productRepository.findAllByParentCategory(category, hidden);
        }
        return productRepository.findAllByCategoryAndCatalog(category, catalog, hidden);
    }

    @Override
    public List<ProductEntity> findRecommendedProducts(final Long pid, final int count) {
        return productRepository.findAll().stream().limit(count).collect(Collectors.toList());
    }

    @Override
    public void promoteProduct(Long pid) {
        final ProductEntity product = productRepository.findOne(pid);
        product.setPromoted(!product.getPromoted());
        productRepository.saveAndFlush(product);
    }

    @Override
    public void publishProduct(Long pid) {
        final ProductEntity product = productRepository.findOne(pid);
        product.setHidden(!product.isHidden());
        productRepository.saveAndFlush(product);
    }

}
