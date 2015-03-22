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

    public boolean onActivate(String catId, Long cid) {
        return onActivate(catId, cid, null, null);
    }

    public boolean onActivate(String catId, Long cid, OrderType orderType, Direction direction) {
        CatalogEntity cat = getCatalogService().findCatalog(catId);
        initScrollableContext(cid, cat.getId(), orderType, direction);
        return true;
    }

    public Object[] onPassivate() {
        return new Object[]{
                catalog.getAltId(),
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
        return productEntity -> editedProduct = productEntity;
    }

}
