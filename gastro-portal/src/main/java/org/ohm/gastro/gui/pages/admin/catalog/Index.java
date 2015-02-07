package org.ohm.gastro.gui.pages.admin.catalog;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.TagEntity;
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

    @Property
    @Persist
    private ProductEntity editedProduct;

    @Property
    private TagEntity oneTag;

    @Property
    @Inject
    private Block productsBlock;

    @Property
    @Inject
    private Block productEditBlock;

    @Property
    @InjectComponent
    private Zone productsZone;

    @Component(id = "name", parameters = {"value=object?.name", "validate=maxlength=64,required"})
    private TextField nameField;

    @Component(id = "desc", parameters = {"value=object?.description", "validate=maxlength=512"})
    private TextArea descField;

    @Component(id = "delivery", parameters = {"value=object?.delivery", "validate=maxlength=512"})
    private TextArea dlvrField;

    @Component(id = "payment", parameters = {"value=object?.payment", "validate=maxlength=256"})
    private TextArea pmtField;

    @Cached
    public java.util.List<ProductEntity> getProducts() {
        return getProductService().findAllProducts(null, getObject(), null);
    }

    @Override
    public ServiceCallback<CatalogEntity> getServiceCallback() {
        return new AbstractServiceCallback<CatalogEntity>() {

            @Override
            public CatalogEntity findObject(String id) {
                return getCatalogService().findCatalog(Long.parseLong(id));
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

    public Block onActionFromEditProduct(Long pid) {
        editedProduct = getProductService().findProduct(pid);
        return productEditBlock;
    }

    public Block onActionFromDeleteProduct(Long id) {
        getProductService().deleteProduct(id);
        return productsBlock;
    }

    public Block onActionFromPromoteProduct(Long id) {
        getProductService().promoteProduct(id);
        return productsBlock;
    }

    public Block onActionFromShowProduct(Long id) {
        getProductService().publishProduct(id);
        return productsBlock;
    }

    public java.util.List<TagEntity> getProductTags() {
        return getProductTags(oneProduct);
    }

}
