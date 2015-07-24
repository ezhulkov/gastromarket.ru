package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Products extends BaseComponent {

    @Property
    private ProductEntity editedProduct;

    @Property
    private ProductEntity oneProduct;

//    @Inject
//    @Property
//    private Block productEditBlock;

    @Property
    private CatalogEntity catalog;

    public void onActivate(String catId) {
        this.catalog = getCatalogService().findCatalog(catId);
    }

    public Object[] onPassivate() {
        return new Object[]{catalog.getId()};
    }

    public String getTitle() {
        return catalog.getName();
    }

    @Cached
    public boolean isCatalogOwner() {
        return catalog.getUser().equals(getAuthenticatedUserOpt().orElse(null));
    }

}
