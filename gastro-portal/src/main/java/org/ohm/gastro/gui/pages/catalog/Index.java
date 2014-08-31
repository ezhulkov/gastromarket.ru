package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Index extends BaseComponent {

    @Property
    private ProductEntity product;

    @Property
    private TagEntity oneTag;

    public Class onActivate(Long id) {
        product = getCatalogService().findProduct(id);
        return null;
    }

    @Cached
    public java.util.List<TagEntity> getProductTags() {
        return getProductTags(product);
    }

}
