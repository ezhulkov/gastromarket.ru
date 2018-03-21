package org.ohm.gastro.gui.pages.catalog;

import com.google.common.collect.Lists;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OfferEntity;
import org.ohm.gastro.gui.dto.Breadcrumb;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Offers extends AbstractOfferPage {

    @Property
    private CatalogEntity catalog;

    @Inject
    @Property
    private Block offersBlock;

    public Object onActivate(String catId) {
        this.catalog = getCatalogService().findCatalog(catId);
        if (catalog == null) return new HttpError(404, "Page not found.");
        return null;
    }

    public Object[] onPassivate() {
        return catalog == null ? null : new Object[]{catalog.getAltId()};
    }

    @Cached
    public java.util.List<OfferEntity> getAllOffers() {
        return getOfferService().findAllOffers(catalog);
    }

    @Cached
    public boolean isCatalogOwner() {
        return catalog.getUser().equals(getAuthenticatedUserOpt().orElse(null));
    }

    @Override
    public java.util.List<Breadcrumb> getBreadcrumbsContext() {
        return Lists.newArrayList(
                mainPage,
                Breadcrumb.of(getMessages().get(List.class.getName()), List.class),
                Breadcrumb.of(catalog.getName(), Index.class, catalog.getAltId()),
                Breadcrumb.of(getMessages().get(Offers.class.getName()), Offers.class, catalog.getAltId())
        );
    }

}
