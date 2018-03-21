package org.ohm.gastro.gui.pages.catalog;

import com.google.common.collect.Lists;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.OfferEntity;
import org.ohm.gastro.gui.dto.Breadcrumb;

import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Offer extends AbstractOfferPage {

    @Property
    @Inject
    private Block offerBlock;


    public Object onActivate(String oid) {
        offer = getOfferService().findOffer(oid);
        if (offer == null) return new HttpError(404, "Page not found.");
        return null;
    }

    public String onPassivate() {
        return offer == null ? null : offer.getAltId();
    }

    @Cached
    public boolean isCatalogOwner() {
        return offer.getCatalog().getUser().equals(getAuthenticatedUserOpt().orElse(null));
    }

    @Cached
    public java.util.List<OfferEntity> getOffers() {
        return getOfferService().findAllOffers(offer.getCatalog()).stream().filter(t -> !t.equals(offer)).collect(Collectors.toList());
    }

    public String getKeywords() {
        return getMessages().format("page.keywords.offer", offer.getName());
    }

    @Override
    public String getTitle() {
        return offer == null ? "" : offer.getName();
    }

    @Override
    public java.util.List<Breadcrumb> getBreadcrumbsContext() {
        return Lists.newArrayList(
                mainPage,
                Breadcrumb.of(getMessages().get(List.class.getName()), List.class),
                Breadcrumb.of(offer.getCatalog().getName(), Index.class, offer.getCatalog().getAltId()),
                Breadcrumb.of(getMessages().get(Offers.class.getName()), Offers.class, offer.getCatalog().getAltId()),
                Breadcrumb.of(getTitle(), this.getClass())
        );
    }

}
