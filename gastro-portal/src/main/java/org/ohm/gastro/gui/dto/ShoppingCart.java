package org.ohm.gastro.gui.dto;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderProductEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 12.10.14.
 */
public final class ShoppingCart {

    private boolean justAdded;
    private Block basketBlock;
    private final List<OrderProductEntity> products = Lists.newLinkedList();

    public Block getBasketBlock() {
        return basketBlock;
    }

    public void setBasketBlock(Block basketBlock) {
        this.basketBlock = basketBlock;
        this.justAdded = false;
    }

    public boolean isJustAdded() {
        return justAdded;
    }

    public List<OrderProductEntity> getProducts() {
        return ImmutableList.copyOf(products);
    }

    public void addProduct(OrderProductEntity product) {
        final OrderProductEntity purchaseItem = findPurchaseItem(product.getEntity().getId())
                .orElseGet(() -> {
                    products.add(product);
                    return product;
                });
        purchaseItem.setCount(purchaseItem.getCount() + 1);
        justAdded = true;
    }

    public void removeProduct(Long pid) {
        findPurchaseItem(pid).ifPresent(products::remove);
    }

    public void incProduct(Long pid) {
        findPurchaseItem(pid).ifPresent(t -> t.setCount(t.getCount() + 1));
    }

    public void decProduct(Long pid) {
        findPurchaseItem(pid).ifPresent(t -> t.setCount(Math.max(1, t.getCount() - 1)));
    }

    public void purge() {
        products.clear();
    }

    @Cached
    public Integer getTotalPrice() {
        return products.stream().collect(Collectors.summingInt(t -> t.getPrice() * t.getCount()));
    }

    @Cached
    public List<Map.Entry<CatalogEntity, List<OrderProductEntity>>> getCatalogs() {
        return Lists.newArrayList(products.stream().collect(Collectors.groupingBy(t -> t.getEntity().getCatalog())).entrySet());
    }

    private Optional<OrderProductEntity> findPurchaseItem(Long pid) {
        return products.stream().filter(t -> t.getEntity().getId().equals(pid)).findFirst();
    }

}
