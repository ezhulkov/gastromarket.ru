package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Orders extends BaseComponent {

    @Inject
    @Property
    private Block ordersBlock;

    @Inject
    @Property
    private Block orderBlock;

    @Property
    private OrderEntity oneOrder;

    @Property
    private OrderProductEntity oneProduct;

    @Property
    @Persist
    private OrderEntity.Status filter;

    public String getMenuActivation() {
        return filter == null ? "ALL" : filter.name();
    }

    public List<OrderEntity> getOrders() {
        if (isCook()) return getCatalogService().findAllCatalogs(getAuthenticatedUser()).stream()
                .flatMap(t -> getOrderService().findAllOrders(t, filter).stream())
                .sorted((t1, t2) -> t2.getDate().compareTo(t1.getDate()))
                .collect(Collectors.toList());
        return getOrderService().findAllOrders(getAuthenticatedUser(), null);
    }

    public Block onActionFromNew() {
        this.filter = Status.NEW;
        return ordersBlock;
    }

    public Block onActionFromAccepted() {
        this.filter = Status.ACCEPTED;
        return ordersBlock;
    }

    public Block onActionFromReady() {
        this.filter = Status.READY;
        return ordersBlock;
    }

    public Block onActionFromAll() {
        this.filter = null;
        return ordersBlock;
    }

    public int getTotalPrice() {
        return getOrderService().getProductsPrice(oneOrder.getProducts());
    }

    public Block onActionFromDeleteProduct(Long oid, Long pid) {
        oneOrder = getOrderService().findOrder(oid);
        if (!isStatusChangeAllowed()) return orderBlock;
        getOrderService().deleteProduct(oid, pid);
        return orderBlock;
    }

    public Block onActionFromIncProduct(Long oid, Long pid) {
        oneOrder = getOrderService().findOrder(oid);
        if (!isStatusChangeAllowed()) return orderBlock;
        getOrderService().incProduct(oid, pid);
        return orderBlock;
    }

    public Block onActionFromDecProduct(Long oid, Long pid) {
        oneOrder = getOrderService().findOrder(oid);
        if (!isStatusChangeAllowed()) return orderBlock;
        getOrderService().decProduct(oid, pid);
        if (oneOrder.getUsedBonuses() > 0) {
            int newPrice = getOrderService().getProductsPrice(oneOrder.getProducts());
            if (oneOrder.getUsedBonuses() > newPrice) {
                oneOrder.setUsedBonuses(newPrice);
                getOrderService().saveOrder(oneOrder);
            }
        }
        return orderBlock;
    }

    public String getOrderZoneId() {
        return "zoneId" + oneOrder.getId();
    }

    public String getOneOrderStatus() {
        return getMessages().get(oneOrder.getStatus().name());
    }

    public boolean isStatusChangeAllowed() {
        return isStatusChangeAllowed(oneOrder);
    }

    private boolean isStatusChangeAllowed(OrderEntity order) {
        return isCook() && (order.getStatus() == Status.ACCEPTED || order.getStatus() == Status.NEW);
    }

    public Block onActionFromChangeStatusAccepted(Long oid) {
        return changeStatus(oid, Status.ACCEPTED);
    }

    public Block onActionFromChangeStatusCancelled(Long oid) {
        return changeStatus(oid, Status.CANCELLED);
    }

    public Block onActionFromChangeStatusReady(Long oid) {
        return changeStatus(oid, Status.READY);
    }

    private Block changeStatus(Long oid, OrderEntity.Status status) {
        final OrderEntity order = getOrderService().findOrder(oid);
        if (!isStatusChangeAllowed(order)) return ordersBlock;
        getOrderService().changeStatus(order, status);
        return ordersBlock;
    }

    public String getOneProductUnit() {
        return getMessages().get(oneProduct.getProduct().getUnit().name()).toLowerCase();
    }

}
