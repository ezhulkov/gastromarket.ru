package org.ohm.gastro.gui.pages.admin.catalog;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
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
    private TagEntity oneTag;

    @Property
    @Inject
    private Block productsBlock;

    @Property
    @InjectComponent
    private Zone productsZone;

    @Component(id = "name", parameters = {"value=object?.name", "validate=maxlength=64,required"})
    private TextField nameField;

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
