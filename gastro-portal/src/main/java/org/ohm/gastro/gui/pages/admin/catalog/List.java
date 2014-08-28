package org.ohm.gastro.gui.pages.admin.catalog;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class List extends BaseComponent {

    @Property
    private CatalogEntity oneCatalog;

    @Cached
    public java.util.List<CatalogEntity> getCatalogs() {
        return getUserService().findAllCatalogs();
    }

    public void onActionFromDelete(Long id) {
        getUserService().deleteCatalog(id);
    }

}
