package org.ohm.gastro.gui.components;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;
import java.util.Random;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Product extends BaseComponent {

    private final Random random = new Random();

    @Property
    @Parameter(name = "product", required = true, allowNull = false)
    private ProductEntity product;

    @Property
    private TagEntity oneTag;

    public int getHeight() {
        return 270 - random.nextInt(50);
    }

    public List<TagEntity> getProductTags() {
        return getProductTags(product);
    }

    public Block onActionFromPurchase(Long pid) {
        getShoppingCart().addProduct(getCatalogService().findProduct(pid));
        return getShoppingCart().getBasketBlock();
    }

    public String getDescription() {
        String desc = (String) ObjectUtils.defaultIfNull(product.getDescription(), "");
        desc = desc.replaceAll("\\n", "<br/>");
        return desc;
    }

    public boolean isHasBlock2() {
        return StringUtils.isNotEmpty(product.getDescription()) || getProductTags().size() > 0;
    }

}

