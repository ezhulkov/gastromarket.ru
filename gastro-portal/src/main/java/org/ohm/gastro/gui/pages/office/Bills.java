package org.ohm.gastro.gui.pages.office;

import com.google.common.collect.Lists;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.BillEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Bills extends BaseComponent {

    @Property
    private BillEntity bill;

    @Cached
    public List<BillEntity> getBills() {
        return getAuthenticatedUser().getFirstCatalog().map(t -> getBillService().findAllBills(t)).orElseGet(Lists::newLinkedList);
    }

}
