package org.ohm.gastro.gui.pages.office;

import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Contract extends BaseComponent {

    public Class onActionFromAccept() {
        getAuthenticatedUser().getFirstCatalog().ifPresent(t -> {
            t.setContractSigned(true);
            getCatalogService().saveCatalog(t);
        });
        return org.ohm.gastro.gui.pages.Index.class;
    }


}
