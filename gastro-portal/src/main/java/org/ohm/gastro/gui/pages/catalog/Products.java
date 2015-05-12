package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.ScrollableProducts;
import org.ohm.gastro.service.ProductService.OrderType;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Products extends ScrollableProducts {

    @Property
    private ProductEntity editedProduct;

    @Property
    private ProductEntity oneProduct;

    @Inject
    @Property
    private Block productEditBlock;

    public boolean onActivate(String catId) {
        return onActivate(catId, null);
    }

    public boolean onActivate(String catId, Long pid) {
        return onActivate(catId, pid, null, null);
    }

    public boolean onActivate(String catId, Long pid, OrderType orderType, Direction direction) {
        CatalogEntity cat = getCatalogService().findCatalog(catId);
        initScrollableContext(pid, cat.getId(), orderType, direction);
        return true;
    }

    public Object[] onPassivate() {
        return new Object[]{
                catalog.getAltId(),
                propertyValue == null ? null : propertyValue.getId(),
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
        return productEntity -> editedProduct = productEntity;
    }

    @Override
    protected List<ProductEntity> getProductsInternal() {
        return isCatalogOwner() ? getProductService().findAllProducts(propertyValue, catalog) : super.getProductsInternal();
    }

}
