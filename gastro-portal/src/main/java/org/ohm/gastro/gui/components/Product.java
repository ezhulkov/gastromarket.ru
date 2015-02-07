package org.ohm.gastro.gui.components;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
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
    @Parameter(name = "class", required = false, allowNull = true, value = "floating", defaultPrefix = BindingConstants.LITERAL)
    private String additionalClass;

    @Property
    @Parameter(name = "edit", required = false, allowNull = true, defaultPrefix = BindingConstants.LITERAL)
    private boolean editMode = false;

    @Property
    private TagEntity oneTag;

    @Inject
    private Block recommendedBlock;

    @Property
    private List<ProductEntity> recommendedProducts;

    @Property
    private ProductEntity oneProduct;

    public int getHeight() {
        return 270 - random.nextInt(50);
    }

    public List<TagEntity> getProductTags() {
        return getProductTags(product);
    }

    public Block onActionFromPurchase(Long pid) {
        getShoppingCart().addProduct(getProductService().findProduct(pid));
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

    public Block onActionFromRecommended(Long pid, int count) {
        recommendedProducts = getProductService().findRecommendedProducts(pid, count);
        return recommendedBlock;
    }

}