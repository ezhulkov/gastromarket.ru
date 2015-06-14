package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.OfferEntity;

import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Offer extends AbstractOfferPage {

    @Property
    @Inject
    private Block offerBlock;

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

    @Cached
    public java.util.List<OfferEntity> getOffers() {
        return getOfferService().findAllOffers(offer.getCatalog()).stream().filter(t -> !t.equals(offer)).collect(Collectors.toList());
    }

}
