package org.ohm.gastro.gui.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PurchaseEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.office.Order;

import java.util.List;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class OrderShowCatalog extends BaseComponent {

    public enum Type {
        SHORT, FULL
    }

    @Parameter
    @Property
    private CatalogEntity catalog;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    @Property
    private Type type = Type.SHORT;

    @Property
    private OrderProductEntity item;

    @Property
    @Inject
    private Block orderShowCatalogBlock;

    @Property
    private int index;

    @Property
    private OrderEntity preOrder;

    @Component(id = "comment", parameters = {"value=preOrder.comment", "validate=maxlength=1024"})
    private TextArea commentField;

    @Component(id = "deliveryAddress", parameters = {"value=preOrder.customer.deliveryAddress", "validate=required"})
    private TextArea dAddressField;

    @Component(id = "mobilePhone", parameters = {"value=preOrder.customer.mobilePhone", "validate=required"})
    private TextField mobileField;

    @Component(id = "fullName", parameters = {"value=preOrder.customer.fullName", "validate=required"})
    private TextField fNameField;

    @Component(id = "dueDate", parameters = {"value=preOrder.dueDate"})
    private TextField dueDateField;

    @Component(id = "promoCode", parameters = {"value=preOrder.promoCode"})
    private TextField promoCodeField;

    public String getItemIndex() {
        return String.format("%s.", index + 1);
    }

    public boolean isNewOrder() {
        return getItems().size() == 1;
    }

    public List<OrderProductEntity> getItems() {
        return getShoppingCart().getItems(catalog);
    }

    public Block onActionFromDeleteItem(Long cId, PurchaseEntity.Type type, Long id, Long mid) {
        this.catalog = getCatalogService().findCatalog(cId);
        getShoppingCart().removeItem(type, id, mid);
        return orderShowCatalogBlock;
    }

    public String getProductUnit() {
        if (item.getEntity().getType() == PurchaseEntity.Type.PRODUCT) return getMessages().format(((ProductEntity) item.getEntity()).getUnit().name() + "_TEXT",
                                                                                                   ((ProductEntity) item.getEntity()).getUnitValue()).toLowerCase();
        return null;
    }

    public int getTotal() {
        return getShoppingCart().getCatalogPrice(catalog);
    }

    public String getRowClass() {
        return index % 2 == 0 ? "odd" : "even";
    }

    public Object[] getDeleteContext() {
        return new Object[]{
                catalog.getId(),
                item.getEntity().getType(),
                item.getEntity().getId(),
                item.getModifier() == null ? null : item.getModifier().getId()
        };
    }

    public boolean isFull() {
        return type == Type.FULL;
    }

    public String getItemPage() {
        if (item.getEntity().getType() == PurchaseEntity.Type.PRODUCT) return "/product/" + item.getEntity().getAltId();
        return "/catalog/offer/" + item.getEntity().getAltId();
    }

    public String getOrderShowCatalogZoneId() {
        return "orderShowCatalogZoneId" + catalog.getId();
    }

    public void onPrepareFromOrderForm() {
        if (isAuthenticated()) {
            preOrder = new OrderEntity();
            preOrder.setCustomer(getAuthenticatedUser());
        }
    }

    public Object onSuccessFromOrderForm(Long cId) {
        final CatalogEntity catalog = getCatalogService().findCatalog(cId);
        final List<OrderProductEntity> items = getShoppingCart().getItems(catalog);
        preOrder.setProducts(getShoppingCart().getItems(catalog));
        final OrderEntity order = getOrderService().placeOrder(preOrder);
        getShoppingCart().removeItems(items);
        return getPageLinkSource().createPageRenderLinkWithContext(Order.class, order.getId(), true);
    }

}
