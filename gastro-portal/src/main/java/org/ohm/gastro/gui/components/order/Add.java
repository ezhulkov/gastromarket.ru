package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.PriceModifierEntity;
import org.ohm.gastro.domain.PriceModifierEntity.Sign;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PurchaseEntity;
import org.ohm.gastro.domain.PurchaseEntity.Type;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class Add extends BaseComponent {

    @Parameter
    @Property
    private PurchaseEntity entity;

    @Parameter
    @Property
    private java.util.List<PriceModifierEntity> priceModifiers;

    @Property
    private PriceModifierEntity priceModifier;

    public void onActionFromAjaxLinkPurchase(PurchaseEntity.Type eType, Long eId, Long mId) {
        getShoppingCart().addItem(createPurchaseItem(eType, eId, mId));
        getAjaxResponseRenderer()
                .addRender("basketZone", getShoppingCart().getBasketBlock())
                .addRender("orderShowZone", getShoppingCart().getOrderShowBlock());
    }

    public void onActionFromAjaxLinkPurchase2(PurchaseEntity.Type eType, Long eId) {
        onActionFromAjaxLinkPurchase(eType, eId, null);
    }

    public void onActionFromAjaxLinkPurchase3(PurchaseEntity.Type eType, Long eId) {
        onActionFromAjaxLinkPurchase(eType, eId, null);
    }

    public String getEntityTypeText() {
        return getMessages().get("choose." + entity.getType().name().toLowerCase());
    }

    public String getProductUnit() {
        if (entity.getType() == Type.PRODUCT) return getMessages().format(((ProductEntity) entity).getUnit().name() + "_TEXT",
                                                                          ((ProductEntity) entity).getUnitValue()).toLowerCase();
        return null;
    }

    public String getModifiedPrice() {
        return "" + (entity.getPrice() + (priceModifier.getSign() == Sign.MINUS ? -priceModifier.getPrice() : priceModifier.getPrice()));
    }

}
