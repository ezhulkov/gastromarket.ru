package org.ohm.gastro.gui.pages.product;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Search extends BaseComponent {

    @Property
    private String searchString;

    @Property
    private ProductEntity oneProduct;

    public boolean onActivate() {
        return true;
    }

    public boolean onActivate(String searchString) {
        this.searchString = searchString;
        return true;
    }

    public String onPassivate() {
        return searchString;
    }

    public java.util.List<ProductEntity> getProducts() {
        return getProductService().searchProducts(searchString, 0, 100);
    }

}
