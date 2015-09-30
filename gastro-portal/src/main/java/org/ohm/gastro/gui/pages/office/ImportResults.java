package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.catalog.Wizard;

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

    public Object onActivate(Long cid) {
        catalog = getCatalogService().findCatalog(cid);
        if (!catalog.isWasSetup()) return getPageLinkSource().createPageRenderLinkWithContext(Wizard.class, catalog.getId());
        return true;
    }

    public Long onPassivate() {
        return catalog == null ? null : catalog.getId();
    }

    public List<ProductEntity> getProducts() {
        return getProductService().findAllRawProducts(catalog, 0, 100);
    }

}
