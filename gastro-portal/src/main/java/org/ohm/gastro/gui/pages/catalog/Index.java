package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Index extends BaseComponent {

    @Property
    private boolean edit;

    @Property
    private ProductEntity product;

    @Property
    private TagEntity oneTag;

    public boolean onActivate(Long id) {
        return onActivate(id, "");
    }

    public boolean onActivate(Long id, String edit) {
        this.product = getCatalogService().findProduct(id);
        this.edit = "edit".equals(edit);
        return true;
    }

    @Cached
    public java.util.List<TagEntity> getProductTags() {
        return getProductTags(product);
    }

    public boolean isOwner() {
        UserEntity user = getAuthenticatedUser();
        return user != null && product.getCatalog().getUser().equals(user);
    }

}
