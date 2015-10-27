package org.ohm.gastro.service;

import org.javatuples.Tuple;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.PriceModifierEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.PurchaseEntity;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.reps.ProductRepository;
import org.ohm.gastro.service.social.MediaElement;
import org.springframework.data.domain.Sort.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface ProductService extends ImageUploaderService, AltIdService<ProductEntity, ProductRepository> {

    int PRODUCTS_PER_PAGE = 8;

    enum OrderType {
        NAME, PRICE, NONE, POSITION
    }

    List<ProductEntity> findAllRawProducts(@Nonnull CatalogEntity catalog, final int from, final int to);

    int findAllCategoryProductsCount(@Nullable CatalogEntity catalog, @Nullable PropertyValueEntity category);

    int findAllProductsCountCached(final CatalogEntity catalog, @Nonnull PropertyValueEntity category);

    PriceModifierEntity findPriceModifier(Long id);

    void attachPriceModifiers(PurchaseEntity object, List<PriceModifierEntity> submittedModifiers);

    void productPosition(List<Long> collect, String type);

    void hideProduct(Long id);

    List<ProductEntity> findProductsForFrontend(final PropertyValueEntity property, final CatalogEntity catalog, final Boolean wasSetup,
                                                final Boolean hidden, final OrderType orderType, final Direction direction, String positionType, final int from, final int to);

    int findProductsForFrontendCount(final CatalogEntity catalog);

    ProductEntity findProduct(final Long id);

    ProductEntity findProduct(final String altId);

    List<ProductEntity> findPromotedProducts();

    List<TagEntity> findAllTags(final ProductEntity product);

    void deleteProduct(final Long id, final CatalogEntity catalog);

    ProductEntity saveProduct(final ProductEntity product, final Map<Long, String> propValues, final List<Tuple> listValues);

    ProductEntity createProduct(final ProductEntity product, final CatalogEntity catalog);

    ProductEntity saveProduct(final ProductEntity product);

    List<ProductEntity> searchProducts(String query, final int from, final int to);

    List<ProductEntity> findRecommendedProducts(final Long pid, final int count);

    void promoteProduct(final Long pid);

    void importProducts(Map<String, Set<MediaElement>> cachedElements, CatalogEntity catalog);

    List<PriceModifierEntity> findAllModifiers(PurchaseEntity entity);

    List<PropertyValueEntity> findAllRootValues(CatalogEntity catalog, Boolean wasSetup);

}
