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
import org.ohm.gastro.service.ProductService;
import org.ohm.gastro.service.ProductService.OrderType;
import org.springframework.data.domain.Sort.Direction;

public abstract class ScrollableProducts extends BaseComponent {

    @Property
    protected PropertyValueEntity propertyValue = null;

    @Property
    protected PropertyValueEntity parentPropertyValue = null;

    @Property
    protected CatalogEntity catalog = null;

    @Property
    protected Direction direction = null;

    @Property
    protected ProductService.OrderType orderType = OrderType.NONE;

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
    public Block fetchNextProducts(int from, Long pid, OrderType orderType, Direction direction) {
        this.orderType = orderType;
        this.direction = direction;
        this.from = from;
        this.to = from + ProductService.PRODUCTS_PER_PAGE;
        this.propertyValue = pid == null ? null : getPropertyService().findPropertyValue(pid);
        return productsBlock;
    }

    protected java.util.List<ProductEntity> getProductsInternal() {
        return getProductService().findProductsForFrontend(propertyValue, catalog, true, false, orderType, direction, null, from, to);
    }

    public java.util.List<ProductEntity> getProducts() {
        final java.util.List<ProductEntity> products = getProductsInternal();
        hasProducts = !products.isEmpty();
        wasProducts = from != 0 || !products.isEmpty();
        return products;
    }

    protected void initScrollableContext(String ppid, String pid, Long catId, OrderType orderType, Direction direction) {
        final String context = (String) ObjectUtils.defaultIfNull(pid, "empty");
        if (!context.equals(prevContext)) {
            this.from = 0;
            this.to = ProductService.PRODUCTS_PER_PAGE;
            prevContext = context;
        }
        this.propertyValue = pid == null ? null : getPropertyService().findPropertyValue(pid);
        this.parentPropertyValue = ppid == null ? null : getPropertyService().findPropertyValue(ppid);
        this.catalog = catId == null ? null : getCatalogService().findCatalog(catId);
        this.direction = direction;
        this.orderType = orderType;
    }

    public Object[] getFetchContext() {
        return new Object[]{
                to,
                propertyValue == null ? null : propertyValue.getId(),
                orderType,
                direction
        };
    }

}