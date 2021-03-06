package org.ohm.gastro.gui.pages.product;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.filter.RegionFilter;
import org.ohm.gastro.gui.pages.AbstractPage;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Promoted extends AbstractPage {

    @Property
    private ProductEntity oneProduct;

    @Cached
    public java.util.List<ProductEntity> getProducts() {
        return getProductService().findPromotedProducts(RegionFilter.getCurrentRegion());
    }

}
