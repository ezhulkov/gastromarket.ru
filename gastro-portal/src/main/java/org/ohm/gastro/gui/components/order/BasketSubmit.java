package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.pages.office.Order;

import java.io.IOException;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class BasketSubmit extends AbstractOrder {

    @Component(id = "dueDate", parameters = {"value=preOrder.dueDate"})
    private TextField dueDateField;

    @Component(id = "personCount", parameters = {"value=preOrder.personCount"})
    private TextField promoCodeField;

    @Component(id = "deliveryAddress", parameters = {"value=preOrder.customer.deliveryAddress", "validate=required"})
    private TextArea dAddressField;

    @Component(id = "mobilePhone", parameters = {"value=preOrder.customer.mobilePhone", "validate=required"})
    private TextField mobileField;

    @Component(id = "fullName", parameters = {"value=preOrder.customer.fullName", "validate=required"})
    private TextField fNameField;

    @Component(id = "comment", parameters = {"value=preOrder.comment", "validate=maxlength=1024"})
    private TextArea commentField;

    @Property
    private OrderEntity preOrder;

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

    public boolean isTotalEnough() {
        return catalog.getBasketMin() == null || catalog.getBasketMin() <= getShoppingCart().getCatalogPrice(catalog);
    }

    public Object[] getFormContext() {
        return new Object[]{
                catalog == null ? null : catalog.getId(),
                order == null ? null : order.getId()
        };
    }

}
