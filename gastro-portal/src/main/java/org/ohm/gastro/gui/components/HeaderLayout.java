package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.Cart;
import org.ohm.gastro.gui.pages.office.Orders;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class HeaderLayout extends BaseComponent {

    @Property
    private CatalogEntity oneCatalog;

    @Property
    @Parameter(name = "header", required = true)
    private boolean header;

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

    public String getDeclMessages() {
        return getDeclInfo("message", getConversationService().getUnreadMessagesCount(getAuthenticatedUser()));
    }

    public java.util.List getCatalogs() {
        return getCatalogService().findAllCatalogs(getAuthenticatedUserOpt().orElse(null));
    }

    public String getAvatarUrl() {
        return getAuthenticatedUser().getAvatarUrlSmall();
    }

    public Link getCartLink() {
        return isAuthenticated() ?
                getPageLinkSource().createPageRenderLinkWithContext(Orders.class, true, Status.NEW) :
                getPageLinkSource().createPageRenderLink(Cart.class);
    }

    public int getBonuses() {
        return getAuthenticatedUserOpt().map(UserEntity::getBonus).orElse(0);
    }

    public String getBonusesMessage() {
        final int bonuses = getBonuses();
        return getDeclInfo("bonus", bonuses);
    }

}
