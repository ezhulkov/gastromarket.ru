package org.ohm.gastro.gui.pages.product;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.ProductService;
import org.ohm.gastro.service.ProductService.OrderType;
import org.springframework.data.domain.Sort.Direction;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class List extends BaseComponent {

    @Property
    private CategoryEntity category = null;

    @Property
    private String searchString = "";

    @Property
    private Direction direction = null;

    @Property
    private ProductService.OrderType orderType = OrderType.NONE;

    @Property
    private ProductEntity oneProduct;

    @Property
    private CategoryEntity oneCategory;

    @Inject
    @Property
    private Block productsBlock;

    @Property
    private boolean searchMode = false;

    @Property
    private boolean hasProducts = false;

    @Property
    private int from = 0;

    @Persist
    @Property
    private int to;

    @Persist
    private String prevContext;

    public java.util.List<CategoryEntity> getCategories() {
        return getCatalogService().findAllRootCategories();
    }

    public String getOrderMessage() {
        return getMessages().get(orderType.name());
    }

    public String getCategoryId() {
        return category == null ? "$N" : category.getId().toString();
    }

    public String getCategoryName() {
        return category == null ? getMessages().get("category.select") : category.getName().toLowerCase();
    }

    public java.util.List<ProductEntity> getProducts() {
        final java.util.List<ProductEntity> products = getProductService().findProductsForFrontend(category, null,
                                                                                                   orderType, direction,
                                                                                                   from, to);
        hasProducts = !products.isEmpty();
        return products;
    }

    @OnEvent(value = EventConstants.ACTION, component = "fetchProducts")
    public Block fetchNextProducts(int from) {
        this.from = from;
        this.to = from + ProductService.PRODUCTS_PER_PAGE;
        return productsBlock;
    }

    public boolean onActivate() {
        this.from = 0;
        this.to = ProductService.PRODUCTS_PER_PAGE;
        this.direction = null;
        this.orderType = OrderType.NONE;
        return true;
    }

    public boolean onActivate(Long cid) {
        return onActivate(cid, OrderType.NONE, null);
    }

    public boolean onActivate(String token, String searchString) {
        this.from = 0;
        this.to = ProductService.PRODUCTS_PER_PAGE;
        this.searchString = searchString;
        this.searchMode = true;
        return true;
    }

    public boolean onActivate(Long cid, ProductService.OrderType orderType, Direction direction) {
        final String context = String.format("%s-%s-%s", cid, orderType, direction);
        if (!context.equals(prevContext)) {
            this.from = 0;
            this.to = ProductService.PRODUCTS_PER_PAGE;
            prevContext = context;
        }
        this.category = cid == null ? null : getCatalogService().findCategory(cid);
        this.direction = direction;
        this.orderType = orderType;
        return true;
    }

    public Object[] onPassivate() {
        if (searchString != null) return new Object[]{"search", searchString};
        return new Object[]{
                category == null ? null : category.getId(),
                orderType == null ? null : orderType.name().toLowerCase(),
                direction == null ? null : direction.name().toLowerCase()};
    }

    public String getTitle() {
        return category == null ? getMessages().get("catalog.title") : category.getParent() == null ? category.getName() : category.getParent().getName() + " - " + category.getName();
    }

}
