package org.ohm.gastro.gui.dto;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.domain.PurchaseEntity;
import org.ohm.gastro.domain.PurchaseEntity.Type;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 12.10.14.
 */
public final class ShoppingCart {

    private final static ToIntFunction<OrderProductEntity> SUM_COUNTER = t -> t.getPrice() * t.getCount();

    private OrderProductEntity lastItem;
    private boolean justAdded;
    private Block basketBlock;
    private Block orderShowBlock;
    private final List<OrderProductEntity> items = Lists.newLinkedList();

    public Block getOrderShowBlock() {
        return orderShowBlock;
    }

    public Block getBasketBlock() {
        return basketBlock;
    }

    public void setBasketBlock(Block basketBlock) {
        this.basketBlock = basketBlock;
    }

    public void setOrderShowBlock(final Block orderShowBlock) {
        this.orderShowBlock = orderShowBlock;
    }

    public void setJustAdded(final boolean justAdded) {
        this.justAdded = justAdded;
    }

    public boolean isJustAdded() {
        return justAdded;
    }

    public List<OrderProductEntity> getItems() {
        return ImmutableList.copyOf(items);
    }

    public OrderProductEntity getLastItem() {
        return lastItem;
    }

    public void addItem(OrderProductEntity item) {
        final OrderProductEntity purchaseItem = findPurchaseItem(item.getEntity().getType(),
                                                                 item.getEntity().getId(),
                                                                 item.getModifier() == null ? null : item.getModifier().getId())
                .map(t -> {
                    t.setCount(t.getCount() + 1);
                    return t;
                })
                .orElseGet(() -> {
                    items.add(item);
                    return item;
                });
        justAdded = true;
        lastItem = item;
    }

    public void removeItem(PurchaseEntity.Type type, Long id, Long mId) {
        findPurchaseItem(type, id, mId).ifPresent(t -> {
            t.setCount(t.getCount() - 1);
            if (t.getCount() <= 0) items.remove(t);
        });
    }

    public void incItem(PurchaseEntity.Type type, Long pid, Long mId) {
        findPurchaseItem(type, pid, mId).ifPresent(t -> t.setCount(t.getCount() + 1));
    }

    public void decItem(PurchaseEntity.Type type, Long pid, Long mId) {
        findPurchaseItem(type, pid, mId).ifPresent(t -> t.setCount(Math.max(1, t.getCount() - 1)));
    }

    public void purge() {
        items.clear();
    }

    @Cached
    public Integer getTotalPrice() {
        return items.stream().collect(Collectors.summingInt(SUM_COUNTER));
    }

    public Integer getCatalogPrice(CatalogEntity catalog) {
        return getItems(catalog).stream().collect(Collectors.summingInt(SUM_COUNTER));
    }

    public List<OrderProductEntity> getItems(CatalogEntity catalog) {
        return items.stream().filter(t -> t.getEntity().getCatalog().equals(catalog)).collect(Collectors.toList());
    }

    public List<OrderProductEntity> getOfferItems(CatalogEntity catalog) {
        return getItems().stream().filter(t -> t.getEntity().getType() == Type.OFFER).collect(Collectors.toList());
    }

    public List<OrderProductEntity> getProductItems(CatalogEntity catalog) {
        return getItems().stream().filter(t -> t.getEntity().getType() == Type.PRODUCT).collect(Collectors.toList());
    }

    @Cached
    public List<Map.Entry<CatalogEntity, List<OrderProductEntity>>> getCatalogs() {
        return Lists.newArrayList(items.stream().collect(Collectors.groupingBy(t -> t.getEntity().getCatalog())).entrySet());
    }

    public Optional<OrderProductEntity> findPurchaseItem(PurchaseEntity.Type type, Long id, Long mId) {
        return items.stream().filter(t -> t.equals(type, id, mId)).findAny();
    }

    public void removeItems(final List<OrderProductEntity> items) {
        items.forEach(item -> removeItem(item.getEntity().getType(),
                                         item.getEntity().getId(),
                                         item.getModifier() == null ? null : item.getModifier().getId()));
    }

}
