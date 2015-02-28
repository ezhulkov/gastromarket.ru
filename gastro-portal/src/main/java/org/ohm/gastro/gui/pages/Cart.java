package org.ohm.gastro.gui.pages;

import org.apache.commons.lang.ObjectUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.domain.UserEntity.Status;
import org.ohm.gastro.domain.UserEntity.Type;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;
import java.util.Map;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Cart extends BaseComponent {

    @Property
    private OrderProductEntity oneProduct;

    @Property
    private Map.Entry<CatalogEntity, List<OrderProductEntity>> oneCatalog;

    @Property
    private OrderEntity newPurchase;

    @Property
    private Integer bonus;

    @Inject
    @Property
    private Block purchaseBlock;

    @Component(id = "comment", parameters = {"value=newPurchase.comment"})
    private TextArea commentField;

    @Component(id = "fullName", parameters = {"value=newPurchase.customer.fullName", "validate=required"})
    private TextField fnField;

    @Component(id = "deliveryAddress", parameters = {"value=newPurchase.customer.deliveryAddress", "validate=required"})
    private TextArea daField;

    @Component(id = "mobilePhone", parameters = {"value=newPurchase.customer.mobilePhone", "validate=required"})
    private TextField mfField;

    @Component(id = "bonus", parameters = {"value=bonus"})
    private TextField bonusField;

    @InjectComponent
    private Form cartForm;

    public Block onActionFromDeleteProduct(Long pid) {
        getShoppingCart().removeProduct(pid);
        return purchaseBlock;
    }

    public Block onActionFromIncProduct(Long pid) {
        getShoppingCart().incProduct(pid);
        return purchaseBlock;
    }

    public Block onActionFromDecProduct(Long pid) {
        getShoppingCart().decProduct(pid);
        return purchaseBlock;
    }

    public Object onSuccessFromCartForm() {
        if (isAuthenticated() && bonus != null && bonus > getAuthenticatedUser().getBonus()) {
            cartForm.recordError(bonusField, getMessages().get("insufficient.bonuses.error"));
            return null;
        }
        newPurchase.setUsedBonuses((Integer) ObjectUtils.defaultIfNull(bonus, 0));
        getOrderService().placeOrder(newPurchase, getShoppingCart().getProducts(), newPurchase.getCustomer());
        getShoppingCart().purge();
        return CartResults.class;
    }

    public void onPrepare() {
        newPurchase = new OrderEntity();
        if (isAuthenticated()) {
            newPurchase.setCustomer(getAuthenticatedUser());
        } else {
            UserEntity newUser = new UserEntity();
            newUser.setType(Type.USER);
            newUser.setAnonymous(true);
            newUser.setStatus(Status.ENABLED);
            newPurchase.setCustomer(newUser);
        }
    }

    public String getOneCatalogDelivery() {
        String desc = (String) ObjectUtils.defaultIfNull(oneCatalog.getKey().getDelivery(), "");
        desc = desc.replaceAll("\\n", "<br/>");
        return desc;
    }

    public String getOneCatalogPayment() {
        String desc = (String) ObjectUtils.defaultIfNull(oneCatalog.getKey().getPayment(), "");
        desc = desc.replaceAll("\\n", "<br/>");
        return desc;
    }

    public String getBonusMessage() {
        if (isAuthenticated()) return getAuthenticatedUser().getBonus() > 0 ?
                getMessages().format("bonuses.message", getAuthenticatedUser().getBonus()) :
                getMessages().get("no.bonuses");
        return getMessages().get("no.bonuses");
    }

}
