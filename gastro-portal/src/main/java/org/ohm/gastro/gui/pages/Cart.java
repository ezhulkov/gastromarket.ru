package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.gui.pages.office.order.List;

import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Cart extends AbstractPage {

    @Property
    private CatalogEntity catalog;

    public Object onActivate() {
        if (isAuthenticated()) return getPageLinkSource().createPageRenderLinkWithContext(List.class, Status.NEW);
        return true;
    }

    public java.util.List<CatalogEntity> getCatalogs() {
        return getShoppingCart().getCatalogs().stream().map(Entry::getKey).collect(Collectors.toList());
    }

}
