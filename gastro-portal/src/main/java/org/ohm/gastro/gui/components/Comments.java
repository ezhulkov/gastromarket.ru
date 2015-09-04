package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.office.Order;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 14.02.15.
 */
public class Comments extends BaseComponent {

    @Property
    @Parameter
    private Block replyBlock;

    @Property
    @Parameter
    private List<CommentEntity> comments;

    @Property
    @Parameter(value = "false")
    private boolean reply;

    @Property
    @Parameter
    private OrderEntity order;

    @Property
    private CommentEntity comment;

    @Property
    private CommentEntity childComment;

    @Property
    private PhotoEntity photo;

    @Property
    private String replyText;

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

    @Cached(watch = "comment")
    public java.util.List<CommentEntity> getChildren() {
        return getRatingService().findAllComments(comment).stream().sorted((o1, o2) -> o1.getDate().compareTo(o2.getDate())).collect(Collectors.toList());
    }

    public boolean isOrderUser() {
        return order != null && order.getCustomer().equals(getAuthenticatedUserOpt().orElse(null)) && order.getCatalog() == null;
    }

    public Link onActionFromAttachCook(Long cid) {
        final Link link = getFirstCatalog(getUserService().findUser(cid)).map(catalog -> {
            getOrderService().attachTender(catalog, order, getAuthenticatedUser());
            return getPageLinkSource().createPageRenderLinkWithContext(Order.class, true, order.getId(), false);
        }).orElse(null);
        return link;
    }

    public boolean isReplyAllowed() {
        return order != null && order.getCustomer().equals(getAuthenticatedUserOpt().orElse(null)) || comment.getAuthor().equals(getAuthenticatedUserOpt().orElse(null));
    }

    public Block onSubmitFromReplyForm(Long oId, Long cId) {
        final CommentEntity comment = getRatingService().findComment(cId);
        order = getOrderService().findOrder(oId);
        getRatingService().placeReply(comment, getAuthenticatedUser(), replyText);
        return replyBlock;
    }

    @Cached(watch = "comment")
    public List<PhotoEntity> getPhotos() {
        return getRatingService().findAllPhotos(comment);
    }

}