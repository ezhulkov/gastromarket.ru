package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.components.Catalog;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Search extends BaseComponent {

    @Property
    private ProductEntity oneProduct;

    private String searchString;

    @Component(id = "catalog")
    private Catalog catalogComponent;

    public boolean onActivate() {
        catalogComponent.activate(null, null, null, true);
        return true;
    }

    public boolean onActivate(String searchString) {
        this.searchString = searchString;
        catalogComponent.activate(null, null, searchString, true);
        return true;
    }

    public Object[] onPassivate() {
        return catalogComponent.getSearchString() == null ? null : new Object[]{catalogComponent.getSearchString()};
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
        catalogComponent.activate(null, null, searchString, true);
    }
}
