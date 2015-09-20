package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Cached;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.catalog.Wizard;
import org.ohm.gastro.gui.pages.office.Import;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class WizardInvitation extends BaseComponent {

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
