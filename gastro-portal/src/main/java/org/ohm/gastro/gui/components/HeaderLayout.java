package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.Region;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.filter.RegionFilter;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.Cart;
import org.ohm.gastro.gui.pages.office.order.List;

import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class HeaderLayout extends BaseComponent {

    @Property
    private CatalogEntity oneCatalog;

    @Property
    @Parameter(name = "header", required = true)
    private boolean header;

    @Property
    private Region region;

    @Inject
    @Property
    private Block orderShowBlock;

    public void beginRender() {
        getShoppingCart().setOrderShowBlock(orderShowBlock);
        getShoppingCart().setJustAdded(false);
    }

    public String getDeclProducts() {
        return getDeclInfo("cart", getShoppingCart().getCatalogs().size());
    }

    @Cached
    public int getUnreadMessagesCount() {
        return getConversationService().getUnreadMessagesCount(getAuthenticatedUser());
    }

    @Cached
    public long getActiveOrdersCount() {
        if (!isAuthenticated()) return 0;
        return (isCook() ?
                getCatalogService().findAllCatalogs(getAuthenticatedUser()).stream().flatMap(c -> getOrderService().findAllOrders(c).stream()).collect(Collectors.toList()) :
                getOrderService().findAllOrders(getAuthenticatedUser()))
                .stream()
                .filter(o -> o.getMetaStatus() == Status.ACTIVE)
                .count();
    }

    public java.util.List getCatalogs() {
        return getCatalogService().findAllCatalogs(getAuthenticatedUserOpt().orElse(null));
    }

    public String getAvatarUrl() {
        return getAuthenticatedUser().getAvatarUrlSmall();
    }

    public Link getCartLink() {
        return isAuthenticated() ?
                getPageLinkSource().createPageRenderLinkWithContext(List.class, true, Status.NEW) :
                getPageLinkSource().createPageRenderLink(Cart.class);
    }

    public int getBonuses() {
        return getAuthenticatedUserOpt().map(UserEntity::getBonus).orElse(0);
    }

    public String getBonusesMessage() {
        final int bonuses = getBonuses();
        return getDeclInfo("bonus", bonuses);
    }

    public String getRegionPrintable() {
        return getMessages().get("Region." + region);
    }

    public Region[] getRegions() {
        return Region.values();
    }

    public boolean isCurrent() {
        return RegionFilter.getCurrentRegion() == region;
    }

}
