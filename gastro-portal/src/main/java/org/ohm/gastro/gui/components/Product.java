package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Product extends BaseComponent {

    @Property
    @Parameter(name = "product", required = true, allowNull = false)
    private ProductEntity product;

    @Property
    @Parameter(name = "owner", required = false, allowNull = true)
    private Boolean owner;

    @Property
    private TagEntity oneTag;

    public List<TagEntity> getProductTags() {
        return getProductTags(product);
    }

}

