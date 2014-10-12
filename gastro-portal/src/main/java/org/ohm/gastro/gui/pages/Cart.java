package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
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

    @Component(id = "comment", parameters = {"value=newPurchase.comment"})
    private TextArea commentField;

    @Component(id = "fullName", parameters = {"value=newPurchase.customer.fullName"})
    private TextField fnField;

    @Component(id = "deliveryAddress", parameters = {"value=newPurchase.customer.deliveryAddress"})
    private TextField daField;

    @Component(id = "mobilePhone", parameters = {"value=newPurchase.customer.mobilePhone"})
    private TextField mfField;

    public void onActionFromDeleteProduct(Long pid) {
        getShoppingCart().removeProduct(new ProductEntity(pid));
    }

    public List<Map.Entry<CatalogEntity, List<ProductEntity>>> getCatalogs() {
        return getShoppingCart().getProducts().stream()
                .map(t -> getCatalogService().findProduct(t.getId()))
                .collect(Collectors.groupingBy(ProductEntity::getCatalog)).entrySet().stream()
                .collect(Collectors.toList());
    }

    public Integer getTotalPrice() {
        return getShoppingCart().getProducts().stream().collect(Collectors.summingInt(ProductEntity::getPrice));
    }

    public Object onSubmitFromCartForm() {
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
