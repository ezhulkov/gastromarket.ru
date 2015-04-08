package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class ImportResults extends BaseComponent {

    @Property
    private CatalogEntity catalog;

    public void onActivate(Long cid) {
        catalog = getCatalogService().findCatalog(cid);
    }

    public Long onPassivate() {
        return catalog == null ? null : catalog.getId();
    }

}
