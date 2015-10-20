package org.ohm.gastro.gui.components.comment;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.CommentableEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.office.Order;

import java.util.Optional;

/**
 * Created by ezhulkov on 14.02.15.
 */
public class Comment extends BaseComponent {

    @Property
    @Parameter
    private CommentEntity comment;

    @Property
    @Parameter
    private CommentableEntity entity;

    @Property
    @Parameter(value = "false")
    private boolean reply;

    @Property
    private String attachReason;

    @Parameter(defaultPrefix = BindingConstants.LITERAL, value = "true")
    private boolean showEdit;

    private Optional<CatalogEntity> getFirstCatalog() {
        return getFirstCatalog(comment.getAuthor());
    }

    private Optional<CatalogEntity> getFirstCatalog(UserEntity cook) {
        return getCatalogService().findAllCatalogs(cook).stream().findFirst();
    }

    public boolean isCookReply() {
        return comment.getAuthor().isCook();
    }

    public boolean isCanEditComment() {
        return showEdit && getAuthenticatedUserOpt().map(t -> t.isAdmin() || t.equals(comment.getAuthor())).orElse(false);
    }

    public boolean isOrderOwner() {
        return getOrder().isOrderOwner(getAuthenticatedUserSafe());
    }

    public Link onSuccessFromAttachTenderAjaxForm(Long cid, Long oid) {
        return getFirstCatalog(getUserService().findUser(cid)).map(catalog -> {
            final OrderEntity tender = getOrderService().findOrder(oid);
            tender.setAttachReason(attachReason);
            getOrderService().attachTender(catalog, tender, getAuthenticatedUser());
            return getPageLinkSource().createPageRenderLinkWithContext(Order.class, true, tender.getId(), false);
        }).orElse(null);
    }

    public OrderEntity getOrder() {
        return (OrderEntity) entity;
    }

    public boolean isAttachedCatalog() {
        return getOrder().isTenderAttached() && getOrder().getCatalog().equals(comment.getAuthor().getFirstCatalog().orElse(null));
    }

}
