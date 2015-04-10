package org.ohm.gastro.service;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.reps.ProductRepository;
import org.ohm.gastro.service.social.MediaElement;
import org.springframework.data.domain.Sort.Direction;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface ProductService extends ImageUploaderService<ProductEntity>, AltIdService<ProductEntity, ProductRepository> {

    int PRODUCTS_PER_PAGE = 8;

    List<ProductEntity> findAllRawProducts(@Nonnull CatalogEntity catalog);

    public enum OrderType {
        NAME, PRICE, NONE
    }

    List<ProductEntity> findAllProducts(final CategoryEntity category, final CatalogEntity catalog);

    List<ProductEntity> findProductsForFrontend(final CategoryEntity category, final CatalogEntity catalog, final OrderType orderType, final Direction direction, final int from, final int to);

    int findProductsForFrontendCount(final CatalogEntity catalog);

    ProductEntity findProduct(final Long id);

    ProductEntity findProduct(final String altId);

    List<ProductEntity> findPromotedProducts();

    List<TagEntity> findAllTags(final ProductEntity product);

    void deleteProduct(final Long id);

    ProductEntity saveProduct(final ProductEntity product, final Map<Long, String> propValues, final Map<Long, String[]> listValues);

    ProductEntity saveProduct(final ProductEntity product);

    List<ProductEntity> searchProducts(String query, final int from, final int to);

    List<ProductEntity> findRecommendedProducts(final Long pid, final int count);

    void promoteProduct(final Long pid);

    void importProducts(Map<String, Set<MediaElement>> cachedElements, CatalogEntity catalog);

}
