package org.ohm.gastro.gui.components.comment;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.Optional;

/**
 * Created by ezhulkov on 14.02.15.
 */
public class Comment extends BaseComponent {

    @Property
    @Parameter
    private CommentEntity comment;

    @Parameter(defaultPrefix = BindingConstants.LITERAL, value = "true")
    private boolean showEdit;

    public String getAvatarUrl() {
        return isCookReply() ?
                getFirstCatalog().map(CatalogEntity::getAvatarUrlMedium).orElse(comment.getAuthor().getAvatarUrlMedium()) :
                comment.getAuthor().getAvatarUrlMedium();
    }

    public String getLinkUrl() {
        return isCookReply() ?
                getFirstCatalog().map(CatalogEntity::getFullUrl).get() :
                comment.getAuthor().getFullUrl();
    }

    public String getLinkName() {
        return isCookReply() ?
                getFirstCatalog().map(CatalogEntity::getName).orElse(comment.getAuthor().getFullName()) :
                comment.getAuthor().getFullName();
    }

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

}
