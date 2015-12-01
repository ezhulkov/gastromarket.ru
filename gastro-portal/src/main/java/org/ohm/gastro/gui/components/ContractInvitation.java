package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Cached;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.office.Import;

import java.util.stream.Stream;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class ContractInvitation extends BaseComponent {

    @Cached
    public boolean getNeedContract() {
        return isCook() &&
                !(getComponentResources().getPage() instanceof Import ||
                        getComponentResources().getPage() instanceof org.ohm.gastro.gui.pages.catalog.Wizard ||
                        getComponentResources().getPage() instanceof org.ohm.gastro.gui.pages.Contract ||
                        getComponentResources().getPage() instanceof org.ohm.gastro.gui.pages.office.Contract) &&
                getAuthenticatedUserOpt()
                        .map(t -> getCatalogService().findAllCatalogs(t).stream())
                        .orElseGet(Stream::empty)
                        .filter(t -> !t.getContractSigned())
                        .count() != 0;
    }

}
