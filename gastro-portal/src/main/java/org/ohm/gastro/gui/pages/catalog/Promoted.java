package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Promoted extends BaseComponent {

    @Property
    private ProductEntity oneProduct;

    @Cached
    public java.util.List<ProductEntity> getProducts() {
        return getCatalogService().findPromotedProducts();
    }

}
