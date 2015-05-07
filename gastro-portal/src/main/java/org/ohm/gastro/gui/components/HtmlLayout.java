package org.ohm.gastro.gui.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.catalog.Wizard;
import org.ohm.gastro.gui.pages.office.Import;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class HtmlLayout extends BaseComponent {

    @Property
    @Parameter(name = "title", required = false, value = "page.title", defaultPrefix = BindingConstants.MESSAGE)
    private String title;

    @Property
    @Parameter(name = "header", required = false, value = "true", defaultPrefix = BindingConstants.PROP)
    private boolean header;

    @Cached
    public CatalogEntity getNewCatalog() {
        if (!isCook() ||
                getComponentResources().getPage() instanceof Wizard ||
                getComponentResources().getPage() instanceof Import) return null;
        return getCatalogService().findAllCatalogs(getAuthenticatedUser()).stream()
                .filter(t -> !t.isWasSetup())
                .findAny().orElse(null);
    }

}
