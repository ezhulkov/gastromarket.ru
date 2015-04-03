package org.ohm.gastro.gui.components;

import org.apache.commons.collections.CollectionUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

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

    public void beginRender() {
        getShoppingCart().setBasketBlock(basketBlock);
    }

    public int getUnreadMessages() {
        return getMessageService().getUnreadMessages(getAuthenticatedUser());
    }

    public String getDeclProducts() {
        if (getShoppingCart().getProducts().size() == 1) return getMessages().get("one.product");
        if (getShoppingCart().getProducts().size() % 10 < 5) return getMessages().get("four.products");
        return getMessages().get("many.products");
    }

    public String getBonusesMessage() {
        final int bonuses = getBonuses();
        if (bonuses == 0) return getMessages().get("no.bonuses");
        if (bonuses == 1) return getMessages().format("one.bonus", bonuses);
        if (bonuses % 10 < 5) return getMessages().format("four.bonuses", bonuses);
        return getMessages().get("many.bonuses");
    }

    public List<CatalogEntity> getCatalogs() {
        return getCatalogService().findAllCatalogs(getAuthenticatedUserOpt().orElse(null));
    }

    public String getHidden() {
        return isCook() ? "hidden" : "";
    }

    public boolean isShowBonuses() {
        return isUser();
    }

    public int getBonuses() {
        return getAuthenticatedUserOpt().map(t -> getUserService().getUserBonuses(t)).orElse(0);
    }

    public String getAvatarUrl() {
        if (!isCook()) {
            return getAuthenticatedUser().getAvatarUrlSmall();
        } else {
            final UserEntity user = getAuthenticatedUser();
            final List<CatalogEntity> allCatalogs = getCatalogService().findAllCatalogs(user);
            if (CollectionUtils.isEmpty(allCatalogs)) return user.getAvatarUrlSmall();
            else return allCatalogs.get(0).getAvatarUrlSmall();
        }
    }

}
