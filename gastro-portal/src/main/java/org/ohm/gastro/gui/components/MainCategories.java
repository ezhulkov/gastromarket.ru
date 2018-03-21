
package org.ohm.gastro.gui.components;

import com.google.common.cache.LoadingCache;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.PropertyValueEntity.Tag;
import org.ohm.gastro.domain.Region;
import org.ohm.gastro.filter.RegionFilter;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 30.05.15.
 */
public class MainCategories extends Section {

    @Property
    private PropertyValueEntity onePropertyValue;

    private final LoadingCache<Region, List<PropertyValueEntity>> cachedCategories = createCache(
            (Function<Region, List<PropertyValueEntity>>) key -> getPropertyService().findAllValues(Tag.ROOT).stream().filter(PropertyValueEntity::isMainPage).collect(Collectors.toList()),
            1, TimeUnit.HOURS);

    private final LoadingCache<Region, List<PropertyValueEntity>> cachedEventValues = createCache(
            (Function<Region, List<PropertyValueEntity>>) key -> getPropertyService().findAllValues(Tag.EVENT).stream().filter(PropertyValueEntity::isMainPage).collect(Collectors.toList()),
            1, TimeUnit.HOURS);

    @Cached
    public List<PropertyValueEntity> getEventValues() {
        return cachedEventValues.getUnchecked(RegionFilter.getCurrentRegion());
    }

    public boolean isShowCategory() {
        return getProductService().findAllProductsCountCached(null, onePropertyValue, RegionFilter.getCurrentRegion()) > 0;
    }

    @Cached
    public List<PropertyValueEntity> getCategoryValues() {
        return cachedCategories.getUnchecked(RegionFilter.getCurrentRegion());
    }


}
