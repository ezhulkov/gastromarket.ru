package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class ImportResults extends BaseComponent {

    @Property
    private CatalogEntity catalog;

    @Property
    private ProductEntity oneProduct;

    @Inject
    @Property
    private Block productsBlock;

    public void onActivate(Long cid) {
        catalog = getCatalogService().findCatalog(cid);
    }

    public Long onPassivate() {
        return catalog == null ? null : catalog.getId();
    }

    public List<ProductEntity> getProducts() {
        return getProductService().findAllRawProducts(catalog);
    }

}
