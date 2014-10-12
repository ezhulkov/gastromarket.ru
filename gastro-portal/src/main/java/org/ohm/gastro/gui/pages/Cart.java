package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Cart extends BaseComponent {

    @Property
    private ProductEntity oneProduct;

    public void onActionFromDeleteProduct(Long pid) {
        getShoppingCart().removeProduct(new ProductEntity(pid));
    }

}
