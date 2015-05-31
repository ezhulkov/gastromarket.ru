package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.OfferEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.08.14.
 */
public abstract class AbstractOfferPage extends BaseComponent {

    @Property
    protected OfferEntity offer;

    @Property
    private ProductEntity product;

    public String getTitle() {
        return offer.getName();
    }

    @Cached
    public boolean isCatalogOwner() {
        return offer.getCatalog().getUser().equals(getAuthenticatedUserOpt().orElse(null));
    }

    @Cached(watch = "offer")
    public java.util.List<ProductEntity> getProducts() {
        return getProductService().findAllProducts(offer);
    }

}
