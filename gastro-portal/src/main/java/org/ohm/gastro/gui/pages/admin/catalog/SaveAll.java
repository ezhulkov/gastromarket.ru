package org.ohm.gastro.gui.pages.admin.catalog;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class SaveAll extends BaseComponent {

    @Property
    private Integer count;

    public void onActivate() {
        getCatalogService().findAllCatalogs().forEach(catalog -> getCatalogService().saveCatalog(catalog));
    }

}
