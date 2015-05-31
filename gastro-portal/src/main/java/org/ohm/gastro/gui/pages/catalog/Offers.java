package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OfferEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Offers extends BaseComponent {

    @Property
    private CatalogEntity catalog;

    @Property
    private OfferEntity offer;

    @Property
    private ProductEntity product;

    public void onActivate(String catId) {
        this.catalog = getCatalogService().findCatalog(catId);
    }

    public Object[] onPassivate() {
        return new Object[]{catalog.getAltId()};
    }

    public String getTitle() {
        return catalog.getName();
    }

    @Cached
    public boolean isCatalogOwner() {
        return catalog.getUser().equals(getAuthenticatedUserOpt().orElse(null));
    }

    @Cached
    public java.util.List<OfferEntity> getAllOffers() {
        return getOfferService().findAllOffers(catalog);
    }

    @Cached(watch = "offer")
    public java.util.List<ProductEntity> getProducts() {
        return getProductService().findAllProducts(offer);
    }

    @Cached(watch = "offer")
    public String getMainProductAvatarUrl() {
        java.util.List<ProductEntity> allProducts = getProductService().findAllProducts(offer);
        return allProducts.isEmpty() ? "/img/offer-stub-270x270.jpg" : allProducts.get(0).getAvatarUrlMedium();
    }

}
