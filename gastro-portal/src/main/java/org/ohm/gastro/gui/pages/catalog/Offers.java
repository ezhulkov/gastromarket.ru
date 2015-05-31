package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OfferEntity;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Offers extends AbstractOfferPage {

    @Property
    private CatalogEntity catalog;

    public void onActivate(String catId) {
        this.catalog = getCatalogService().findCatalog(catId);
    }

    public Object[] onPassivate() {
        return new Object[]{catalog.getAltId()};
    }

    @Override
    public String getTitle() {
        return catalog.getName();
    }

    @Cached
    public java.util.List<OfferEntity> getAllOffers() {
        return getOfferService().findAllOffers(catalog);
    }

}
