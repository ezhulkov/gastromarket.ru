package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.OrderEntity.Type;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.Arrays;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class OrderChangeStatus extends BaseComponent {

    @Property
    @Parameter
    private OrderEntity order;

    @Property
    @Parameter
    private String orderShowCatalogZoneId;

    @Parameter
    private Block orderBlock;

    @Property
    private Status status;

    @Property
    private CatalogEntity catalog;

    public void beginRender() {
        catalog = order.getCatalog();
    }

    public boolean isCanChangeState() {
        return order != null && getStatuses().length > 0;
    }

    public Status[] getStatuses() {
        return isCook() ? order.getStatus().getCookGraph() : order.getStatus().getClientGraph();
    }

    public String getOrderStatusText() {
        if (order.getType() == Type.PRIVATE) {
            return getMessages().format("order.status." + order.getStatus()).toLowerCase();
        } else {
            return getMessages().format("tender.status." + order.getStatus()).toLowerCase();
        }
    }

    public String getStatusTitle() {
        return getMessages().format("order.title." + status);
    }

    public String getStatusAction() {
        return getMessages().format("order.action." + status);
    }

    public boolean isHasCancel() {
        return Arrays.asList(getStatuses()).contains(Status.CANCELLED);
    }

    public Block onActionFromStatusChange(Long oId, Status status) {
        this.order = getOrderService().findOrder(oId);
        this.catalog = order.getCatalog();
        getOrderService().changeStatus(order, status, catalog, getAuthenticatedUser());
        return orderBlock;
    }

}
