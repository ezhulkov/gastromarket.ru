package org.ohm.gastro.gui.pages.catalog;

import org.apache.commons.lang.ObjectUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
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

    public boolean onActivate(Long pid) {
        product = getCatalogService().findProduct(pid);
        return true;
    }

    public Object[] onPassivate() {
        return new Object[]{product.getId()};
    }

    @Cached
    public java.util.List<ProductEntity> getRecommendedProducts() {
        return getCatalogService().findRecommendedProducts(product.getId(), 4);
    }

    public Block onActionFromPurchase(Long pid) {
        getShoppingCart().addProduct(getCatalogService().findProduct(pid));
        return getShoppingCart().getBasketBlock();
    }

    @Cached
    public String getDescription() {
        String desc = (String) ObjectUtils.defaultIfNull(product.getDescription(), "");
        desc = desc.replaceAll("\\n", "<br/>");
        return desc;
    }

    public java.util.List<TagEntity> getProductTags() {
        return getProductTags(product);
    }

}
