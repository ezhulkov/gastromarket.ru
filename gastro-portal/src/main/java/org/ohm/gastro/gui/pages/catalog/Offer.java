package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.OfferEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Offer extends BaseComponent {

    @Property
    private OfferEntity offer;

    public void onActivate(String oid) {
        this.offer = getOfferService().findOffer(oid);
    }

    public Object[] onPassivate() {
        return new Object[]{offer.getAltId()};
    }

    public String getTitle() {
        return offer.getName();
    }

    @Cached
    public boolean isCatalogOwner() {
        return offer.getCatalog().getUser().equals(getAuthenticatedUserOpt().orElse(null));
    }

}
