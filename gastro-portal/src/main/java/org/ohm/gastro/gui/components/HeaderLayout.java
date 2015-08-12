package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.Cart;
import org.ohm.gastro.gui.pages.office.Orders;

import java.util.List;

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
    private Block basketBlock;

    @Inject
    @Property
    private Block orderShowBlock;

    public void beginRender() {
        getShoppingCart().setBasketBlock(basketBlock);
        getShoppingCart().setOrderShowBlock(orderShowBlock);
        getShoppingCart().setJustAdded(false);
    }

    public int getUnreadMessages() {
        return getMessageService().getUnreadMessages(getAuthenticatedUser());
    }

    public String getDeclProducts() {
        return getDeclInfo("cart", getShoppingCart().getCatalogs().size());
    }

    public List<CatalogEntity> getCatalogs() {
        return getCatalogService().findAllCatalogs(getAuthenticatedUserOpt().orElse(null));
    }

    public String getHidden() {
        return isCook() ? "hidden" : "";
    }

    public String getAvatarUrl() {
        return getAuthenticatedUser().getAvatarUrlSmall();
    }

    public Link getCartLink() {
        return isAuthenticated() ? getPageLinkSource().createPageRenderLink(Orders.class) : getPageLinkSource().createPageRenderLink(Cart.class);
    }

}
