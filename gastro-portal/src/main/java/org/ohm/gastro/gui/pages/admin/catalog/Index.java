package org.ohm.gastro.gui.pages.admin.catalog;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Checkbox;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.AbstractServiceCallback;
import org.ohm.gastro.gui.ServiceCallback;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.EditObjectPage;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Index extends EditObjectPage<CatalogEntity> {

    @Property
    private ProductEntity oneProduct;

    @Component(id = "name", parameters = {"value=object?.name"})
    private TextField nameField;

    @Component(id = "freeMonths", parameters = {"value=object?.freeMonths"})
    private TextField freeMonthsField;

    @Component(id = "desc", parameters = {"value=object?.description"})
    private TextArea descField;

    @Component(id = "delivery", parameters = {"value=object?.delivery"})
    private TextArea dlvrField;

    @Component(id = "payment", parameters = {"value=object?.payment"})
    private TextArea pmtField;

    @Component(id = "cert1", parameters = {"value=object?.cert1"})
    private Checkbox cert1;

    @Component(id = "cert2", parameters = {"value=object?.cert2"})
    private Checkbox cert2;

    @Component(id = "cert3", parameters = {"value=object?.cert3"})
    private Checkbox cert3;

    @Component(id = "contractSigned", parameters = {"value=object?.contractSigned"})
    private Checkbox contractSigned;

    @Cached
    public java.util.List<ProductEntity> getProducts() {
        return getProductService().findProductsForFrontend(null, getObject(), null, null, null, null, null, null, 0, Integer.MAX_VALUE);
    }

    @Override
    public ServiceCallback<CatalogEntity> getServiceCallback() {
        return new AbstractServiceCallback<CatalogEntity>() {

            @Override
            public CatalogEntity findObject(String id) {
                return getCatalogService().findCatalog(id);
            }

            @Override
            public Class<? extends BaseComponent> deleteObject(CatalogEntity object) {
                getCatalogService().deleteCatalog(object.getId());
                return List.class;
            }

            @Override
            public Class<? extends BaseComponent> updateObject(CatalogEntity object) {
                getCatalogService().saveCatalog(object);
                return Index.class;
            }
        };
    }

    public void onActionFromPromoteProduct(Long id) {
        getProductService().promoteProduct(id);
    }

    public void onActionFromHideProduct(Long id) {
        getProductService().hideProduct(id);
    }

}
