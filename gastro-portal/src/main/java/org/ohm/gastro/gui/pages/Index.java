package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Index extends BaseComponent {

    @Property
    private CatalogEntity oneCook;

    @Property
    private OrderEntity oneTender;

    @Property
    private ProductEntity oneProduct;

    public Object onActivate(EventContext context) {
        if (context.getCount() == 0) return null;
        return new HttpError(404, "Page not found.");
    }

    @Cached
    public List<CatalogEntity> getCooks() {
        return getCatalogService().findAllActiveCatalogs().stream()
                .sorted(((o1, o2) -> o2.getRating().compareTo(o1.getRating())))
                .limit(5).collect(Collectors.toList());
    }

    @Cached
    public List<OrderEntity> getTenders() {
        return getOrderService().findAllTenders().stream()
                .filter(t -> t.getStatus() == Status.NEW)
                .sorted(((o1, o2) -> o1.getDate().compareTo(o2.getDate())))
                .limit(3).collect(Collectors.toList());
    }

    @Cached
    public List<ProductEntity> getProducts() {
        return getProductService().findPromotedProducts().stream().limit(4).collect(Collectors.toList());
    }

}
