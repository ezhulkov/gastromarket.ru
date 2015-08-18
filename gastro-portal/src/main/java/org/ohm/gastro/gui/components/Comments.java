package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;
import java.util.Optional;

/**
 * Created by ezhulkov on 14.02.15.
 */
public class Comments extends BaseComponent {

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
        return getCatalogService().findAllCatalogs(comment.getAuthor()).stream().findFirst();
    }

    public boolean isCookeReply() {
        return comment.getAuthor().isCook();
    }

    @Cached(watch = "comment")
    public java.util.List<CommentEntity> getChildren() {
        return getRatingService().findAllComments(comment);
    }

    public boolean isOrderUser() {
        return order != null && order.getCustomer().equals(getAuthenticatedUserOpt().orElse(null));
    }

}
