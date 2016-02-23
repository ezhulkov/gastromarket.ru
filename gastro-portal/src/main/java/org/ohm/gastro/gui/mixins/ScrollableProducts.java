package org.ohm.gastro.gui.mixins;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.filter.RegionFilter;
import org.ohm.gastro.gui.pages.AbstractPage;
import org.ohm.gastro.service.ProductService;
import org.ohm.gastro.service.ProductService.OrderType;

public abstract class ScrollableProducts extends AbstractPage {

    @Property
    protected PropertyValueEntity categoryPropertyValue = null;

    @Property
    protected PropertyValueEntity eventPropertyValue = null;

    @Property
    protected PropertyValueEntity parentPropertyValue = null;

    @Property
    protected CatalogEntity catalog = null;

    @Property
    protected boolean hasProducts = false;

    @Property
    protected boolean wasProducts = true;

    @Property
    protected int from = 0;

    @Inject
    @Property
    protected Block productsBlock;

    @Persist
    @Property
    protected int to;

    @Persist
    protected String prevContext;

    @OnEvent(value = EventConstants.ACTION, component = "fetchProductsAjaxLink")
    public Block fetchNextProducts(int from, Long cid, Long eid) {
        this.from = from;
        this.to = from + ProductService.PRODUCTS_PER_PAGE;
        this.categoryPropertyValue = cid == null ? null : getPropertyService().findPropertyValue(cid);
        this.eventPropertyValue = eid == null ? null : getPropertyService().findPropertyValue(eid);
        return productsBlock;
    }

    protected java.util.List<ProductEntity> getProductsInternal() {
        return getProductService().findProductsForFrontend(categoryPropertyValue == null ? eventPropertyValue : categoryPropertyValue,
                                                           catalog,
                                                           true,
                                                           false,
                                                           RegionFilter.getCurrentRegion(),
                                                           OrderType.NONE,
                                                           null,
                                                           null,
                                                           from,
                                                           to);
    }

    public java.util.List<ProductEntity> getProducts() {
        final java.util.List<ProductEntity> products = getProductsInternal();
        hasProducts = !products.isEmpty();
        wasProducts = from != 0 || !products.isEmpty();
        return products;
    }

    protected void initScrollableContext(String ppid, String cid, String eid, Long catId) {
        final String context = ObjectUtils.defaultIfNull(cid, "empty");
        if (!context.equals(prevContext)) {
            this.from = 0;
            this.to = ProductService.PRODUCTS_PER_PAGE;
            prevContext = context;
        }
        this.categoryPropertyValue = cid == null ? null : getPropertyService().findPropertyValue(cid);
        this.eventPropertyValue = eid == null ? null : getPropertyService().findPropertyValue(eid);
        this.parentPropertyValue = ppid == null ? null : getPropertyService().findPropertyValue(ppid);
        this.catalog = catId == null ? null : getCatalogService().findCatalog(catId);
    }

    public Object[] getFetchContext() {
        return new Object[]{
                to,
                categoryPropertyValue == null ? null : categoryPropertyValue.getId(),
                eventPropertyValue == null ? null : eventPropertyValue.getId()
        };
    }

}