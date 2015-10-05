package org.ohm.gastro.gui.components.comment;

import org.apache.tapestry5.Block;
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
public class List extends BaseComponent {

    @Property
    @Parameter
    private Block replyBlock;

    @Property
    @Parameter(allowNull = false, required = true)
    private CommentableEntity entity;

    @Property
    @Parameter(value = "false")
    private boolean reply;

    @Property
    @Parameter
    private OrderEntity order;

    @Property
    private CommentEntity comment;

    @Property
    private String replyText;

    @Property
    private String attachReason;

    public String getAvatarUrl() {
        return isCookeReply() ?
                getFirstCatalog().map(CatalogEntity::getAvatarUrlMedium).orElse(comment.getAuthor().getAvatarUrlMedium()) :
                comment.getAuthor().getAvatarUrlMedium();
    }

    public String getLinkUrl() {
        return isCookeReply() ?
                (getFirstCatalog().map(t -> "/catalog/" + t.getAltId()).orElse("/index")) :
                ("/user/" + comment.getAuthor().getId());
    }

    public String getLinkName() {
        return isCookeReply() ?
                getFirstCatalog().map(CatalogEntity::getName).orElse(comment.getAuthor().getFullName()) :
                comment.getAuthor().getFullName();
    }

    private Optional<CatalogEntity> getFirstCatalog() {
        return getFirstCatalog(comment.getAuthor());
    }

    private Optional<CatalogEntity> getFirstCatalog(UserEntity cook) {
        return getCatalogService().findAllCatalogs(cook).stream().findFirst();
    }

    public boolean isCookeReply() {
        return comment.getAuthor().isCook();
    }

    public boolean isOrderUser() {
        return order != null && order.getCustomer().equals(getAuthenticatedUserOpt().orElse(null)) && order.getCatalog() == null && !order.isTenderExpired();
    }

    public boolean isOrderOpened() {
        return order != null && order.getCatalog() == null && !order.isTenderExpired();
    }

    public Link onSuccessFromAttachTenderAjaxForm(Long cid, Long oid) {
        return getFirstCatalog(getUserService().findUser(cid)).map(catalog -> {
            final OrderEntity tender = getOrderService().findOrder(oid);
            tender.setAttachReason(attachReason);
            getOrderService().attachTender(catalog, tender, getAuthenticatedUser());
            return getPageLinkSource().createPageRenderLinkWithContext(Order.class, true, this.order.getId(), false);
        }).orElse(null);
    }

    public boolean isReplyAllowed() {
        return order != null &&
                !order.isTenderExpired() &&
                (order.getCustomer().equals(getAuthenticatedUserOpt().orElse(null)) ||
                        comment.getAuthor().equals(getAuthenticatedUserOpt().orElse(null)));
    }

    public java.util.List<CommentEntity> getComments() {
        return getRatingService().findAllComments(entity);
    }

}