package org.ohm.gastro.gui.pages.admin.product;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.AltIdBaseEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyValueEntity.Tag;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class List extends BaseComponent {

    @Property
    private ProductEntity oneProduct;

    @Inject
    private Block editBlock;

    @Cached
    public java.util.List<ProductEntity> getProducts() {
        return getProductService().findUncheckedProducts();
    }

    public String getCategories() {
        return oneProduct.getValues().stream()
                .map(TagEntity::getValue)
                .filter(t -> t.getTag() == Tag.ROOT || t.getParents().stream().anyMatch(p -> p.getTag() == Tag.ROOT))
                .map(AltIdBaseEntity::getName)
                .collect(Collectors.joining(", "));
    }

    public void onActionFromAccept(Long id) {
        final ProductEntity product = getProductService().findProduct(id);
        product.setWasChecked(true);
        getProductService().saveProduct(product);
    }

    public Block onActionFromEdit(Long pid) {
        this.oneProduct = getProductService().findProduct(pid);
        return editBlock;
    }

}
