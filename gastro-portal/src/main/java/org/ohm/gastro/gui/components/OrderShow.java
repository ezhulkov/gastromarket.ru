package org.ohm.gastro.gui.components;

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
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PurchaseEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.office.Order;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class OrderShow extends BaseComponent {

    public enum Type {
        SHORT, FULL, EDIT
    }

    @Property
    private Status status;

    @Parameter
    @Property
    private CatalogEntity catalog;

    @Parameter
    @Property
    private OrderEntity order;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    @Property
    private Type type = Type.SHORT;

    @Property
    private OrderProductEntity item;

    @Property
    @Inject
    private Block orderShowCatalogBlock;

    @Inject
    private Block clientEditBlock;

    @Inject
    private Block clientPaymentBlock;

    @Inject
    private Block clientRateCook;

    @Inject
    private Block cookRateClient;

    @Property
    private int index;

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

    public String getItemIndex() {
        return String.format("%s.", index + 1);
    }

    public boolean isNewOrder() {
        return getItems().size() == 1;
    }

    public List<OrderProductEntity> getItems() {
        return order == null ? getShoppingCart().getItems(catalog) : getOrderService().findAllItems(order);
    }

    public void beginRender() {
        if (order != null) catalog = order.getCatalog();
    }

    public Block onActionFromDeleteItem(Long cId, Long oId, Long opId, PurchaseEntity.Type type, Long id, Long mid) {
        this.catalog = getCatalogService().findCatalog(cId);
        this.order = getOrderService().findOrder(oId);
        if (order == null) {
            getShoppingCart().removeItem(type, id, mid);
        } else {
            getOrderService().deleteProduct(oId, opId);
        }
        return orderShowCatalogBlock;
    }

    public String getProductUnit() {
        if (item.getEntity().getType() == PurchaseEntity.Type.PRODUCT) return getMessages().format(((ProductEntity) item.getEntity()).getUnit().name() + "_TEXT",
                                                                                                   ((ProductEntity) item.getEntity()).getUnitValue()).toLowerCase();
        return null;
    }

    public int getTotal() {
        return order == null ? getShoppingCart().getCatalogPrice(catalog) : order.getOrderTotalPrice();
    }

    public String getRowClass() {
        return index % 2 == 0 ? "odd" : "even";
    }

    public Object[] getFormContext() {
        return new Object[]{
                catalog.getId(),
                order == null ? null : order.getId()
        };
    }

    public Object[] getDeleteContext() {
        return new Object[]{
                catalog.getId(),
                order == null ? null : order.getId(),
                item.getId(),
                item.getEntity().getType(),
                item.getEntity().getId(),
                item.getModifier() == null ? null : item.getModifier().getId()
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

    public String getItemPage() {
        if (item.getEntity().getType() == PurchaseEntity.Type.PRODUCT) return "/product/" + item.getEntity().getAltId();
        return "/catalog/offer/" + item.getEntity().getAltId();
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
            Link link = getPageLinkSource().createPageRenderLinkWithContext(Order.class, order.getId(), true);
            getResponse().sendRedirect(link);
        } else {
            getOrderService().saveOrder(order);
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

    public String getOrderStatusText() {
        return getMessages().format("order.status." + order.getStatus()).toLowerCase();
    }

    public boolean isCanChangeState() {
        return order != null && getStatuses().length > 0;
    }

    public boolean isCanEdit() {
        return !isCook() && (order == null || order.getStatus() == Status.ACTIVE || order.getStatus() == Status.NEW);
    }

    public String getStatusTitle() {
        return getMessages().format("order.title." + status);
    }

    public String getStatusAction() {
        return getMessages().format("order.action." + status);
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
        getOrderService().saveOrder(order);
        return orderShowCatalogBlock;
    }

    public Block onActionFromStatusChange(Long oId, Status status) {
        this.order = getOrderService().findOrder(oId);
        this.catalog = order.getCatalog();
        getOrderService().changeStatus(order, status, catalog);
        return orderShowCatalogBlock;
    }

    public boolean isHasCancel() {
        return Arrays.asList(getStatuses()).contains(Status.CANCELLED);
    }

    public boolean isTotalEnough() {
        return catalog.getBasketMin() == null || catalog.getBasketMin() <= getShoppingCart().getCatalogPrice(catalog);
    }

    public Block onActionFromPrepay(Long oId) {
        this.order = getOrderService().findOrder(oId);
        this.catalog = order.getCatalog();
        getOrderService().changeStatus(order, Status.PAID, catalog);
        return orderShowCatalogBlock;
    }

    public long getClientPosRating() {
        return getRatingService().findAllComments(order.getCustomer()).stream().filter(t -> t.getRating() > 0).count();
    }

    public long getClientNegRating() {
        return getRatingService().findAllComments(order.getCustomer()).stream().filter(t -> t.getRating() < 0).count();
    }

}
