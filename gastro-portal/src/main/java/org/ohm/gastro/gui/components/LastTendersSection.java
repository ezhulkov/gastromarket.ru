package org.ohm.gastro.gui.components;

import com.google.common.cache.LoadingCache;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.Region;
import org.ohm.gastro.filter.RegionFilter;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 30.05.15.
 */
public class LastTendersSection extends Section {

    @Property
    private OrderEntity oneTender;

    private final LoadingCache<Region, List<OrderEntity>> cachedTenders = createCache(
            (Function<Region, List<OrderEntity>>) key -> getOrderService().findAllTenders(key).stream()
                    .filter(t -> !(t.isTenderExpired() && t.getCatalog() == null))
                    .filter(OrderEntity::isWasSetup)
                    .sorted(((o1, o2) -> o2.getDate().compareTo(o1.getDate())))
                    .limit(3).collect(Collectors.toList()),
            10, TimeUnit.MINUTES);

    @Cached
    public List<OrderEntity> getTenders() {
        return cachedTenders.getUnchecked(RegionFilter.getCurrentRegion());
    }

}
