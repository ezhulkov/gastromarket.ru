package org.ohm.gastro.gui.pages.product;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.ProductService;
import org.ohm.gastro.service.ProductService.Order;
import org.ohm.gastro.service.ProductService.OrderType;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class List extends BaseComponent {

    @Property
    private CategoryEntity category = null;

    @Property
    private String searchString = null;

    @Property
    private ProductService.Order order = Order.NONE;

    @Property
    private ProductService.OrderType orderType = OrderType.NONE;

    @Property
    private ProductEntity oneProduct;

    @Property
    private CategoryEntity oneCategory;

    @Inject
    @Property
    private Block productsBlock;

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
        return category == null ? getMessages().get("category.select") : category.getName();
    }

    public java.util.List<ProductEntity> getProducts() {
        return getProductService().findAllProducts(category, null, false);
    }

    @OnEvent(value = EventConstants.ACTION, component = "fetchProducts")
    public Block fetchNextProducts(Long count) {
        return productsBlock;
    }

    public boolean onActivate() {
        this.order = Order.NONE;
        this.orderType = OrderType.NONE;
        return true;
    }

    public boolean onActivate(Long cid) {
        return onActivate(cid, OrderType.NONE, Order.NONE);
    }

    public boolean onActivate(String token, String searchString) {
        this.searchString = searchString;
        return true;
    }

    public boolean onActivate(Long cid, ProductService.OrderType orderType, ProductService.Order order) {
        this.category = cid == null ? null : getCatalogService().findCategory(cid);
        this.order = order;
        this.orderType = orderType;
        return true;
    }

    public Object[] onPassivate() {
        if (searchString != null) return new Object[]{"search", searchString};
        return new Object[]{
                category == null ? null : category.getId(),
                orderType == null ? null : orderType.name().toLowerCase(),
                order == null ? null : order.name().toLowerCase()};
    }

    public String getTitle() {
        return category == null ? getMessages().get("catalog.title") : category.getParent() == null ? category.getName() : category.getParent().getName() + " - " + category.getName();
    }


}
