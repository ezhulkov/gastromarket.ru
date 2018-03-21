
package org.ohm.gastro.gui.components;

import com.google.common.cache.LoadingCache;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.Region;
import org.ohm.gastro.filter.RegionFilter;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 30.05.15.
 */
public class PromoProducts extends Section {

    @Property
    private ProductEntity oneProduct;

    private final LoadingCache<Region, List<ProductEntity>> cachedPromoProducts = createCache(
            (Function<Region, List<ProductEntity>>) key -> getProductService().findPromotedProducts(key).stream().limit(4).collect(Collectors.toList()),
            1, TimeUnit.HOURS);

    @Cached
    public List<ProductEntity> getProducts() {
        return cachedPromoProducts.getUnchecked(RegionFilter.getCurrentRegion());
    }

}
