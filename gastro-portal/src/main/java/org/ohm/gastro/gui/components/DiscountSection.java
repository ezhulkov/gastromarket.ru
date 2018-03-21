package org.ohm.gastro.gui.components;

import com.google.common.cache.LoadingCache;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.Region;
import org.ohm.gastro.filter.RegionFilter;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 30.05.15.
 */
public class DiscountSection extends Section {

    @Property
    private CatalogEntity catalog;

    private final LoadingCache<Region, List<CatalogEntity>> cachedDiscountCatalogs = createCache(
            (Function<Region, List<CatalogEntity>>) key -> getCatalogService().findDiscountCatalogs().stream()
                    .filter(CatalogEntity::isDiscountActive)
                    .filter(t -> t.getRegion() == key)
                    .collect(Collectors.toList()),
            1, TimeUnit.HOURS);

    @Cached
    public List<CatalogEntity> getCatalogs() {
        return cachedDiscountCatalogs.getUnchecked(RegionFilter.getCurrentRegion());
    }

}
