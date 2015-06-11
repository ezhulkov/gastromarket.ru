package org.ohm.gastro.gui.pages.product;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Index extends BaseComponent {

    @Property
    private ProductEntity product;

    @Property
    private ProductEntity oneProduct;

    @Property
    private TagEntity oneTag;

    public Object onActivate(String pid) {
        product = getProductService().findProduct(pid);
        if (product == null) return new HttpError(404, "Page not found.");
        return true;
    }

    public Object[] onPassivate() {
        return new Object[]{product.getAltId()};
    }

    @Cached
    public java.util.List<ProductEntity> getRecommendedProducts() {
        return getProductService().findRecommendedProducts(product.getId(), 4);
    }

    public Block onActionFromPurchase(Long pid) {
        getShoppingCart().addProduct(createPurchaseItem(pid));
        return getShoppingCart().getBasketBlock();
    }

    public java.util.List<TagEntity> getProductTags() {
        return getProductTags(product);
    }

    public String getProductUnit() {
        return getMessages().format(product.getUnit().name() + "_TEXT", product.getUnitValue()).toLowerCase();
    }

    @Cached
    public boolean isCatalogOwner() {
        return product.getCatalog().getUser().equals(getAuthenticatedUserOpt().orElse(null));
    }

}
