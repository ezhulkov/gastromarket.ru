package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.08.14.
 */
public abstract class AbstractCatalogPage extends BaseComponent {

    @Property
    protected ProductEntity oneProduct;

    @Property
    protected CatalogEntity catalog;

    @Inject
    @Property
    protected Block productsBlock;

    @Inject
    @Property
    protected Block offersBlock;

    @Component(id = "desc", parameters = {"value=catalog.description", "validate=maxlength=4096,required"})
    private TextArea descField;

    @Component(id = "delivery", parameters = {"value=catalog.delivery", "validate=maxlength=4096,required"})
    private TextArea deliveryField;

    @Component(id = "payment", parameters = {"value=catalog.payment", "validate=maxlength=4096"})
    private TextArea pmtField;

    @Component(id = "name", parameters = {"value=catalog.name", "validate=maxlength=512,required"})
    private TextField nameField;

    @Component(id = "basketMin", parameters = {"value=catalog.basketMin"})
    private TextField bmField;

    @Cached
    public boolean isCatalogOwner() {
        return catalog.getUser().equals(getAuthenticatedUserOpt().orElse(null));
    }

    public String getDescLabel() {
        return getMessages().get("desc.label." + catalog.getType().name().toLowerCase());
    }

    public String getDescText() {
        return getMessages().get("desc.text." + catalog.getType().name().toLowerCase());
    }

    public String getNameLabel() {
        return getMessages().get("name.label." + catalog.getType().name().toLowerCase());
    }

}
