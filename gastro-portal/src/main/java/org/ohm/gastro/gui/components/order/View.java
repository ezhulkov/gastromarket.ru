package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.office.Order;

import java.io.IOException;
import java.util.List;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class View extends BaseComponent {

    public enum Type {
        SHORT, FULL, EDIT
    }

    @Parameter
    @Property
    private CatalogEntity catalog;

    @Parameter(value = "false")
    @Property
    private boolean mainPage;

    @Property
    @Parameter(value = "false")
    private boolean frontend;

    @Parameter
    @Property
    private OrderEntity order;

    @Property
    @Parameter(allowNull = false, required = true)
    private boolean privateOrders;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    @Property
    private Type type = Type.SHORT;

    @Inject
    @Property
    private Block orderBlock;

    @Inject
    private Block deniedOrderBlock;

    @Inject
    private Block clientEditBlock;

    @Inject
    private Block clientPaymentBlock;

    @Inject
    private Block clientRateCook;

    @Inject
    private Block cookRateClient;

    @Inject
    @Property
    private Block editTenderBlock;

    @Property
    private OrderEntity preOrder;

    @Component(id = "comment", parameters = {"value=preOrder.comment", "validate=maxlength=1024"})
    private TextArea commentField;

    @Component(id = "comment2", parameters = {"value=order.comment", "validate=maxlength=1024"})
    private TextArea comment2Field;

    @Component(id = "deliveryAddress", parameters = {"value=preOrder.customer.deliveryAddress", "validate=required"})
    private TextArea dAddressField;

    @Component(id = "mobilePhone", parameters = {"value=preOrder.customer.mobilePhone", "validate=required"})
    private TextField mobileField;

    @Component(id = "fullName", parameters = {"value=preOrder.customer.fullName", "validate=required"})
    private TextField fNameField;

    @Component(id = "deliveryAddress2", parameters = {"value=order.customer.deliveryAddress", "validate=required"})
    private TextArea dAddress2Field;

    @Component(id = "mobilePhone2", parameters = {"value=order.customer.mobilePhone", "validate=required"})
    private TextField mobile2Field;

    @Component(id = "fullName2", parameters = {"value=order.customer.fullName", "validate=required"})
    private TextField fName2Field;

    @Component(id = "dueDate", parameters = {"value=preOrder.dueDate"})
    private TextField dueDateField;

    @Component(id = "promoCode", parameters = {"value=preOrder.promoCode"})
    private TextField promoCodeField;

    @Component(id = "dueDate2", parameters = {"value=order.dueDate"})
    private TextField dueDate2Field;

    @Component(id = "promoCode2", parameters = {"value=order.promoCode"})
    private TextField promoCode2Field;

    public List<OrderProductEntity> getItems() {
        return order == null ? getShoppingCart().getItems(catalog) : getOrderService().findAllItems(order);
    }

    public void beginRender() {
        if (order != null) catalog = order.getCatalog();
    }

    public int getTotal() {
        return order == null ? getShoppingCart().getCatalogPrice(catalog) : order.getTotalPrice();
    }

    public Object[] getFormContext() {
        return new Object[]{
                catalog == null ? null : catalog.getId(),
                order == null ? null : order.getId()
        };
    }

    public boolean isFull() {
        return type == Type.FULL;
    }

    public boolean isEdit() {
        return type == Type.EDIT;
    }

    public boolean isContactsAllowed() {
        return isCook() && order.getStatus().getLevel() >= Status.PAID.getLevel();
    }

    public String getOrderShowCatalogZoneId() {
        return String.format("orderShowCatalogZoneId%s", order == null ? catalog.getId() : order.getId());
    }

    public void onPrepareFromOrderForm() {
        if (isAuthenticated()) {
            preOrder = new OrderEntity();
            preOrder.setCustomer(getAuthenticatedUser());
            preOrder.setCatalog(catalog);
        }
    }

    public void onSuccessFromOrderForm(Long cId, Long oId) throws IOException {
        this.catalog = getCatalogService().findCatalog(cId);
        this.order = getOrderService().findOrder(oId);
        List<OrderProductEntity> items = getItems();
        preOrder.setProducts(items);
        preOrder.setCatalog(catalog);
        if (order == null) {
            final OrderEntity order = getOrderService().placeOrder(preOrder);
            getShoppingCart().removeItems(items);
            final Link link = getPageLinkSource().createPageRenderLinkWithContext(Order.class, true, order.getId(), true);
            getResponse().sendRedirect(link);
        } else {
            getOrderService().saveOrder(order, getAuthenticatedUser());
        }
    }

    public String getBasketMinText() {
        return getMessages().format("basket.min.text", catalog.getBasketMin());
    }

    public String getPrepaymentText() {
        if (catalog.getPrepayment() != null) {
            final float total = getTotal();
            final float prepayment = catalog.getPrepayment();
            return getMessages().format("prepayment.text", catalog.getPrepayment(), (int) Math.ceil(prepayment * total / 100));
        }
        return null;
    }

    public boolean isCanEdit() {
        return !isCook() && (order == null || order.getStatus() == Status.ACTIVE || order.getStatus() == Status.NEW);
    }

    public Status[] getStatuses() {
        return isCook() ? order.getStatus().getCookGraph() : order.getStatus().getClientGraph();
    }

    public Block getEditOrderBlock() {
        if (!isCook()) {
            if (order.getStatus() == Status.CONFIRMED) return clientPaymentBlock;
            if (order.getStatus() == Status.CANCELLED || order.getStatus() == Status.CLOSED) return clientRateCook;
            if (isCanEdit()) return clientEditBlock;
        } else {
            if (order.getStatus() == Status.CANCELLED || order.getStatus() == Status.CLOSED) return cookRateClient;
        }
        return null;
    }

    public void onPrepareFromOrderDetailsForm(Long oId) {
        this.order = getOrderService().findOrder(oId);
        this.catalog = order.getCatalog();
    }

    public Block onSuccessFromOrderDetailsForm(Long oId) {
        getOrderService().saveOrder(order, getAuthenticatedUser());
        return orderBlock;
    }

    public boolean isTotalEnough() {
        return catalog.getBasketMin() == null || catalog.getBasketMin() <= getShoppingCart().getCatalogPrice(catalog);
    }

    public Block onActionFromPrepay(Long oId) {
        this.order = getOrderService().findOrder(oId);
        this.catalog = order.getCatalog();
        getOrderService().changeStatus(order, Status.PAID, catalog, getAuthenticatedUser());
        return orderBlock;
    }

    public long getClientPosRating() {
        return getRatingService().findAllComments(order.getCustomer()).stream().filter(t -> t.getRating() > 0).count();
    }

    public long getClientNegRating() {
        return getRatingService().findAllComments(order.getCustomer()).stream().filter(t -> t.getRating() < 0).count();
    }

    public boolean isTender() {
        return order != null && order.getType() == OrderEntity.Type.PUBLIC && order.getCatalog() == null;
    }

    public Block getCurrentOrderBlock() {
        if (order != null && order.getType() == OrderEntity.Type.PUBLIC) return orderBlock;
        if (isAuthenticated()) {
            return order == null || order.isAllowed(getAuthenticatedUser()) ? orderBlock : deniedOrderBlock;
        } else {
            return deniedOrderBlock;
        }
    }

    public String getEditZoneId() {
        return "editZone" + order.getId();
    }

    public Block onActionFromEditTender(Long tid) {
        this.order = getOrderService().findOrder(tid);
        return editTenderBlock;
    }

    public boolean isCanEditTender() {
        return isAuthenticated() && order != null && order.getCustomer() != null && order.getCustomer().equals(getAuthenticatedUser()) && order.getStatus() == Status.ACTIVE;
    }

}
