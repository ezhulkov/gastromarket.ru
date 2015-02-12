package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.ProductService;
import org.ohm.gastro.service.ProductService.OrderType;
import org.springframework.data.domain.Sort.Direction;

import java.util.function.Consumer;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Products extends BaseComponent {

    @Property
    private CatalogEntity catalog = null;

    @Property
    private CategoryEntity category = null;

    @Property
    private Direction direction = null;

    @Property
    private OrderType orderType = OrderType.NONE;

    @Property
    private ProductEntity editedProduct;

    @Property
    private ProductEntity oneProduct;

    @Inject
    @Property
    private Block productsBlock;

    @Inject
    @Property
    private Block productEditBlock;

    @Property
    private int offset = ProductService.PRODUCTS_PER_PAGE;

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
        return getProductService().findProductsForFrontend(category, catalog,
                                                           orderType, direction,
                                                           offset, ProductService.PRODUCTS_PER_PAGE);
    }

    @OnEvent(value = EventConstants.ACTION, component = "fetchProducts")
    public Block fetchNextProducts(int offset) {
        this.offset = offset;
        return productsBlock;
    }

    public boolean onActivate(Long catId) {
        return onActivate(catId, null, OrderType.NONE, null);
    }

    public boolean onActivate(Long catId, Long cid, OrderType orderType, Direction direction) {
        this.catalog = getCatalogService().findCatalog(catId);
        this.category = cid == null ? null : getCatalogService().findCategory(cid);
        this.direction = direction;
        this.orderType = orderType;
        return true;
    }

    public Object[] onPassivate() {
        return new Object[]{
                catalog.getId(),
                category == null ? null : category.getId(),
                orderType == null ? null : orderType.name().toLowerCase(),
                direction == null ? null : direction.name().toLowerCase()};
    }

    public String getTitle() {
        return catalog.getName();
    }

    @Cached
    public boolean isCatalogOwner() {
        return catalog.getUser().equals(getAuthenticatedUserOpt().orElse(null));
    }

    public Consumer<ProductEntity> getProductSetter() {
        return productEntity -> {
            editedProduct = productEntity;
        };
    }

}
