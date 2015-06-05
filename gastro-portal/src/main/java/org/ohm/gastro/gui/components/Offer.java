package org.ohm.gastro.gui.components;

import com.google.common.collect.Lists;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.OfferEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 30.05.15.
 */
public class Offer extends BaseComponent {

    public enum Type {
        SHORT, LIST, FULL
    }

    @Parameter
    @Property
    private OfferEntity offer;

    @Parameter
    @Property
    private boolean editMode;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    @Property
    private Type type;

    @Parameter(name = "offersBlock")
    private Block offersBlock;

    @Inject
    @Property
    private Block offerBlock;

    @Inject
    @Property
    private Block editBlock;

    @Property
    private ProductEntity product;

    @Cached(watch = "offer")
    public java.util.List<ProductEntity> getProducts() {
        return type == Type.SHORT ? Lists.newArrayList() : getProductService().findAllProducts(offer);
    }

    public String getAdditionalClass() {
        return type.name().toLowerCase();
    }

    public String getAvatarUrl() {
        return type == Type.SHORT ? offer.getMainProductAvatarSmall() : offer.getMainProductAvatarBig();
    }

    public String getLeftBlock() {
        return type == Type.SHORT ? "col-sm-2" : "col-sm-3";
    }

    public String getRightBlock() {
        return type == Type.SHORT ? "col-sm-10" : "col-sm-9";
    }

    public Block onActionFromDelete(Long oid) {
        getOfferService().deleteOffer(oid);
        return offersBlock;
    }

    public Block onActionFromEdit(Long pid) {
        this.offer = getOfferService().findOffer(pid);
        return editBlock;
    }

    public String getEditZoneId() {
        return "editZone" + offer.getId();
    }

    public String getOfferZoneId() {
        return "offerZone" + offer.getId();
    }

    public Block onActionFromDeleteProduct(Long pid, Long oid) {
        offer = getOfferService().findOffer(oid);
        offer.getProducts().remove(getProductService().findProduct(pid));
        getOfferService().saveOffer(offer);
        return offerBlock;
    }

    public boolean isMainPage() {
        return type == Type.FULL;
    }

}
