package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.PriceEntity;
import org.ohm.gastro.domain.PriceEntity.Type;
import org.ohm.gastro.domain.PriceModifierEntity;
import org.ohm.gastro.domain.PriceModifierEntity.Sign;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class OrderAdd extends BaseComponent {

    @Parameter
    @Property
    private PriceEntity entity;

    @Property
    private PriceModifierEntity priceModifier;

    public Block onActionFromPurchase(Long pid) {
        getShoppingCart().addProduct(createPurchaseItem(pid));
        return getShoppingCart().getBasketBlock();
    }

//    public Block onActionFromPurchase2(Long pid) {
//        return onActionFromPurchase(pid);
//    }

    @Cached(watch = "entity")
    public java.util.List<PriceModifierEntity> getPriceModifiers() {
        return getProductService().findAllModifiers(entity).stream().filter(t -> t.getPrice() != 0).collect(Collectors.toList());
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
