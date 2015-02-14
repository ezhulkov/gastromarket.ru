package org.ohm.gastro.gui.mixins;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.service.ProductService;
import org.ohm.gastro.service.ProductService.OrderType;
import org.springframework.data.domain.Sort.Direction;

public abstract class ScrollableProducts extends BaseComponent {

    @Property
    protected CategoryEntity category = null;

    @Property
    protected CatalogEntity catalog = null;

    @Property
    protected Direction direction = null;

    @Property
    protected ProductService.OrderType orderType = OrderType.NONE;

    @Property
    protected boolean hasProducts = false;

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

    @OnEvent(value = EventConstants.ACTION, component = "fetchProducts")
    public Block fetchNextProducts(int from, Long cid, OrderType orderType, Direction direction) {
        this.orderType = orderType;
        this.direction = direction;
        this.from = from;
        this.to = from + ProductService.PRODUCTS_PER_PAGE;
        this.category = cid == null ? null : getCatalogService().findCategory(cid);
        return productsBlock;
    }

    public java.util.List<ProductEntity> getProducts() {
        final java.util.List<ProductEntity> products = getProductService().findProductsForFrontend(category, catalog, orderType, direction, from, to);
        hasProducts = !products.isEmpty();
        return products;
    }

    protected void initScrollableContext(Long cid, Long catId, OrderType orderType, Direction direction) {
        final String context = cid == null ? "empty" : cid.toString();
        if (!context.equals(prevContext)) {
            this.from = 0;
            this.to = ProductService.PRODUCTS_PER_PAGE;
            prevContext = context;
        }
        this.category = cid == null ? null : getCatalogService().findCategory(cid);
        this.catalog = catId == null ? null : getCatalogService().findCatalog(catId);
        this.direction = direction;
        this.orderType = orderType;
    }

    public Object[] getFetchContext() {
        return new Object[]{
                to,
                category == null ? null : category.getId(),
                orderType,
                direction
        };
    }

}