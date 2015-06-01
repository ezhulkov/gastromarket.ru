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
import org.ohm.gastro.domain.OfferEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.catalog.Offer;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class OfferEdit extends BaseComponent {

    public enum Stage {
        DESC, PRODUCTS
    }

    @Inject
    @Property
    private Block editDescBlock;

    @Inject
    @Property
    private Block editProductsBlock;

    @Property
    private boolean error = false;

    @Property
    private boolean closeImmediately;

    @Property
    private boolean goBack;

    @Component(id = "offerPrice", parameters = {"value=offer.price", "validate=required"})
    private TextField oPriceField;

    @Component(id = "offerName", parameters = {"value=offer.name", "validate=maxlength=64,required"})
    private TextField oNameField;

    @Component(id = "offerDescription", parameters = {"value=offer.description", "validate=maxlength=1024"})
    private TextArea descField;

    @Parameter(name = "catalog", required = true, allowNull = true)
    private CatalogEntity catalog;

    @Parameter(name = "reloadPage", required = false, allowNull = false, value = "false")
    private boolean reloadPage;

    @Parameter(name = "offersBlock", required = false, allowNull = false)
    private Block offersBlock;

    @Parameter(name = "offerBlock", required = false, allowNull = false)
    private Block offerBlock;

    @Parameter(name = "offerZoneId", required = false, allowNull = false)
    private String offerZoneId;

    @Property
    @Parameter(name = "modalId", defaultPrefix = BindingConstants.LITERAL, value = "of-new")
    private String modalId;

    @Property
    @Parameter(name = "edit", defaultPrefix = BindingConstants.LITERAL, value = "true")
    private boolean editOffer;

    @Property
    @Parameter(defaultPrefix = BindingConstants.PROP, allowNull = true, required = false)
    private OfferEntity offer;

    public Long getOfferId() {
        return offer == null || offer.getId() == null ? null : offer.getId();
    }

    public String getOfferEditZone() {
        return editOffer ? "offerEditZone" + offer.getId() : "offerZoneNew";
    }

    public String getEditProductsLabel() {
        return editOffer ? getMessages().get("edit.products") : getMessages().get("add.products");
    }

    //Description section
    public void onPrepareFromDescForm() {
        if (offer == null || offer.getId() == null) {
            offer = new OfferEntity();
            offer.setCatalog(catalog);
        }
    }

    public void onFailureFromDescForm() {
        this.error = true;
    }

    public Object onSubmitFromDescForm(Long oid) {
        if (!error) {
            final OfferEntity origOffer = oid != null ? getOfferService().findOffer(oid) : offer;
            origOffer.setName(offer.getName());
            origOffer.setPrice(offer.getPrice());
            origOffer.setDescription(offer.getDescription());
            offer = getOfferService().saveOffer(origOffer);
            if (offersBlock != null) getAjaxResponseRenderer().addRender("offersZone", offersBlock);
            if (offerBlock != null) getAjaxResponseRenderer().addRender(offerZoneId, offerBlock);
            getAjaxResponseRenderer().addRender(getOfferEditZone(), closeImmediately ? editDescBlock : editProductsBlock);
            if (closeImmediately) offer = null;
            if (closeImmediately && reloadPage) return Offer.class;
        } else {
            closeImmediately = false;
            getAjaxResponseRenderer().addRender(getOfferEditZone(), editDescBlock);
        }
        return null;
    }

    //Properties section
    public void onSelectedFromSaveAndBack2() {
        goBack = true;
    }

    public void onSelectedFromSaveAndClose2() {
        this.closeImmediately = true;
    }

    public Object onSubmitFromProductsForm(Long pid) {
        offer = getOfferService().findOffer(pid);
        if (goBack) {
            getAjaxResponseRenderer().addRender(getOfferEditZone(), editDescBlock);
            return null;
        }
        if (closeImmediately) offer = null;
        if (closeImmediately) getAjaxResponseRenderer().addRender(getOfferEditZone(), editDescBlock);
        return closeImmediately && reloadPage ? Offer.class : null;
    }

}