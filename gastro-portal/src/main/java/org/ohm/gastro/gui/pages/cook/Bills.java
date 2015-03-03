package org.ohm.gastro.gui.pages.cook;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.BillEntity;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;

/**
 * Created by ezhulkov on 03.03.15.
 */
public class Bills extends BaseComponent {

    @Property
    private BillEntity oneBill;

    @Property
    private CatalogEntity oneCatalog;

    public List<BillEntity> getAllBills() {
        return getOrderService().findAllBills(oneCatalog);
    }

    public List<CatalogEntity> getAllCatalogs() {
        return getCatalogService().findAllCatalogs();
    }

    public String getOneBillStatus() {
        return getMessages().get(oneBill.getStatus().name());
    }



}
