package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.annotations.Cached;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Offer extends AbstractOfferPage {

    public void onActivate(String oid) {
        this.offer = getOfferService().findOffer(oid);
    }

    public Object[] onPassivate() {
        return new Object[]{offer.getAltId()};
    }

    @Cached
    public boolean isCatalogOwner() {
        return offer.getCatalog().getUser().equals(getAuthenticatedUserOpt().orElse(null));
    }

}
