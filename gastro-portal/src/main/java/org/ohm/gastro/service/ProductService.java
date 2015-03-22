package org.ohm.gastro.service;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.reps.ProductRepository;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;
import java.util.Map;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface ProductService extends ImageUploaderService<ProductEntity>, AltIdService<ProductEntity, ProductRepository> {

    public static int PRODUCTS_PER_PAGE = 8;

    public enum OrderType {
        NAME, PRICE, NONE
    }

    List<ProductEntity> findAllProducts(CategoryEntity category, CatalogEntity catalog, Boolean hidden);

    List<ProductEntity> findProductsForFrontend(CategoryEntity category, CatalogEntity catalog, OrderType orderType, Direction direction, int from, int to);

    int findProductsForFrontendCount(CatalogEntity catalog);

    ProductEntity findProduct(Long id);

    ProductEntity findProduct(String altId);

    List<ProductEntity> findPromotedProducts();

    List<ProductEntity> findAllProducts();

    List<TagEntity> findAllTags(ProductEntity product, PropertyEntity property);

    List<TagEntity> findAllTags(ProductEntity product);

    void deleteProduct(Long id);

    ProductEntity saveProduct(ProductEntity product, Map<Long, String> propValues, Map<Long, String[]> listValues);

    ProductEntity saveProduct(ProductEntity product);

    List<ProductEntity> searchProducts(String query, final int from, final int to);

    List<ProductEntity> findRecommendedProducts(final Long pid, final int count);

    void promoteProduct(Long pid);

    void publishProduct(Long pid);

}
