package org.ohm.gastro.gui.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentEntity.Type;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 13.08.15.
 */
public class RateCookUser extends BaseComponent {

    @Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private Type type;

    @Property
    @Parameter
    private CatalogEntity catalog;

    @Property
    @Parameter
    private UserEntity user;

    @Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String modalId;

    @Property
    @Parameter
    private boolean commentAllowed;

    @Property
    private boolean opinion = true;

    @Property
    private String rateComment;

    public String getTitle() {
        return getMessages().format("rate.title." + type.name().toLowerCase(), type == Type.CATALOG ? catalog.getName() : user.getFullName());
    }

    public void onSuccessFromRateForm(Long cId, Long uId) {
        if (type == Type.CATALOG) {
            getRatingService().rateCatalog(getCatalogService().findCatalog(cId), rateComment, opinion ? 1 : -1, getAuthenticatedUserOpt().orElse(null));
        } else {
            getRatingService().rateClient(getUserService().findUser(uId), rateComment, opinion ? 1 : -1, getAuthenticatedUserOpt().orElse(null));
        }
    }

    public String getNATitle() {
        return getMessages().format("rate.na.title." + type.name().toLowerCase());
    }

    public boolean getLike() {
        return true;
    }

    public boolean getDislike() {
        return false;
    }

    public Object[] getRateContext() {
        return new Object[]{
                catalog == null ? null : catalog.getId(),
                user == null ? null : user.getId()
        };
    }

}
