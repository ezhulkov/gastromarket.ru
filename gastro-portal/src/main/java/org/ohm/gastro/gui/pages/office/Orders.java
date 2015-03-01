package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;

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
    private OrderEntity.Status filter;

    public String getMenuActivation() {
        return filter == null ? "ALL" : filter.name();
    }

    public List<OrderEntity> getOrders() {
        return getOrderService().findAllOrders(null);
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
        getOrderService().deleteProduct(oid, pid);
        return orderBlock;
    }

    public Block onActionFromIncProduct(Long oid, Long pid) {
        oneOrder = getOrderService().findOrder(oid);
        getOrderService().incProduct(oid, pid);
        return orderBlock;
    }

    public Block onActionFromDecProduct(Long oid, Long pid) {
        oneOrder = getOrderService().findOrder(oid);
        getOrderService().decProduct(oid, pid);
        return orderBlock;
    }

    public String getOrderZoneId() {
        return "zoneId" + oneOrder.getId();
    }


}
