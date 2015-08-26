package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.HttpError;
import org.apache.tapestry5.services.URLEncoder;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.product.Search;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Index extends BaseComponent {

    @Inject
    private URLEncoder urlEncoder;

    @Property
    private String searchString = "";

    @Property
    private PropertyValueEntity onePropertyValue;

    @Property
    private CatalogEntity oneCook;

    @Property
    private ProductEntity oneProduct;

    @Property
    private OrderEntity oneTender;

    public Object onActivate(EventContext context) {
        if (context.getCount() == 0) return null;
        return new HttpError(404, "Page not found.");
    }

    @Cached
    public List<PropertyValueEntity> getPropertyValues() {
        return getPropertyService().findAllValues(PropertyValueEntity.Tag.ROOT);
    }

    @Cached
    public List<CatalogEntity> getCooks() {
        return getCatalogService().findAllActiveCatalogs().stream().
                sorted(((o1, o2) -> o1.getRating().compareTo(o2.getRating()))).
                limit(5).collect(Collectors.toList());
    }

    @Cached
    public List<ProductEntity> getProducts() {
        return getProductService().findPromotedProducts().stream().limit(4).collect(Collectors.toList());
    }

    public Link onSubmitFromSearchForm() throws IOException {
        return getPageLinkSource().createPageRenderLinkWithContext(Search.class, Search.processSearchString(searchString));
    }

    @Cached
    public List<OrderEntity> getTenders() {
        return getOrderService().findAllTenders().stream().
                sorted(((o1, o2) -> o1.getDate().compareTo(o2.getDate()))).
                limit(3).collect(Collectors.toList());
    }

}
