package org.ohm.gastro.gui.pages.product;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.ScrollableProducts;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Search extends ScrollableProducts {

    @Property
    private String searchString;

    @Property
    private ProductEntity oneProduct;

    public boolean onActivate() {
        initScrollableContext(null, null, null, null);
        return true;
    }

    public boolean onActivate(String searchString) {
        initScrollableContext(null, null, null, null);
        this.searchString = searchString;
        return true;
    }

    public String onPassivate() {
        return searchString;
    }

    @Override
    public java.util.List<ProductEntity> getProducts() {
        return getProductService().searchProducts(searchString, from, to);
    }

}
