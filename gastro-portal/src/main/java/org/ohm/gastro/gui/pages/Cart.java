package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PurchaseEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.domain.UserEntity.Status;
import org.ohm.gastro.domain.UserEntity.Type;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Cart extends BaseComponent {

    @Property
    private ProductEntity oneProduct;

    @Property
    private Map.Entry<CatalogEntity, List<ProductEntity>> oneCatalog;

    @Property
    private PurchaseEntity newPurchase;

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
        getShoppingCart().removeProduct(new ProductEntity(pid));
        return purchaseBlock;
    }

    public Block onActionFromIncProduct(Long pid) {
        return purchaseBlock;
    }

    public Block onActionFromDecProduct(Long pid) {
        return purchaseBlock;
    }

    @Cached
    public List<Map.Entry<CatalogEntity, List<ProductEntity>>> getCatalogs() {
        return getShoppingCart().getProducts().stream()
                .map(t -> getProductService().findProduct(t.getId()))
                .collect(Collectors.groupingBy(ProductEntity::getCatalog)).entrySet().stream()
                .collect(Collectors.toList());
    }

    public Integer getTotalPrice() {
        return getShoppingCart().getProducts().stream().collect(Collectors.summingInt(ProductEntity::getPrice));
    }

    public Object onSuccessFromCartForm() {
        if (isAuthenticated() && bonus > getAuthenticatedUser().getBonus()) {
            cartForm.recordError(bonusField, getMessages().get("insufficient.bonuses.error"));
            return null;
        }
        getOrderService().placeOrder(newPurchase, getShoppingCart().getProducts());
        getShoppingCart().purge();
        return CartResults.class;
    }

    public void onPrepare() {
        newPurchase = new PurchaseEntity();
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

}
