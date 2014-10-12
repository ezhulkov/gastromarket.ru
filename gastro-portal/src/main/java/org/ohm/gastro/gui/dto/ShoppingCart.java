package org.ohm.gastro.gui.dto;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.tapestry5.Block;
import org.ohm.gastro.domain.ProductEntity;

import java.util.List;

/**
 * Created by ezhulkov on 12.10.14.
 */
public final class ShoppingCart {

    private Block basketBlock;
    private final List<ProductEntity> products = Lists.newLinkedList();

    public Block getBasketBlock() {
        return basketBlock;
    }

    public void setBasketBlock(Block basketBlock) {
        this.basketBlock = basketBlock;
    }

    public List<ProductEntity> getProducts() {
        return ImmutableList.copyOf(products);
    }

    public void addProduct(ProductEntity product) {
        if (!products.contains(product)) products.add(product);
    }

    public void removeProduct(ProductEntity product) {
        products.remove(product);
    }

}
