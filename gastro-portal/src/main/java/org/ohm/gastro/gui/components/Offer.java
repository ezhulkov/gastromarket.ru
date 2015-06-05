package org.ohm.gastro.gui.components;

import com.google.common.collect.Lists;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.OfferEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 30.05.15.
 */
public class Offer extends BaseComponent {

    @Parameter
    @Property
    private OfferEntity offer;

    @Parameter
    @Property
    private boolean edit;

    @Parameter(defaultPrefix = BindingConstants.LITERAL, value = "false")
    @Property
    private boolean shortVersion;

    @Property
    private ProductEntity product;

    @Cached(watch = "offer")
    public java.util.List<ProductEntity> getProducts() {
        return shortVersion ? Lists.newArrayList() : getProductService().findAllProducts(offer);
    }

    public String getAdditionalClass() {
        return shortVersion ? "short" : "";
    }

    public String getAvatarUrl() {
        return shortVersion ? offer.getMainProductAvatarSmall() : offer.getMainProductAvatarBig();
    }

    public String getLeftBlock() {
        return shortVersion ? "col-sm-2" : "col-sm-3";
    }

    public String getRightBlock() {
        return shortVersion ? "col-sm-10" : "col-sm-9";
    }

}
