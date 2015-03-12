package org.ohm.gastro.gui.pages.cook;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.BillEntity;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.misc.GenericSelectModel;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 03.03.15.
 */
public class Bills extends BaseComponent {

    @Property
    private BillEntity oneBill;

    @Property
    private OrderEntity oneOrder;

    @Property
    private CatalogEntity catalog;

    @Inject
    @Property
    private Block billBlock;

    @Component(id = "catalog", parameters = {"model=catalogModel", "encoder=catalogModel", "value=catalog"})
    private Select pCatalogField;

    public boolean onActivate() {
        catalog = getCatalogService().findAllCatalogs().get(0);
        return true;
    }

    public boolean onActivate(Long cid) {
        this.catalog = getCatalogService().findCatalog(cid);
        return true;
    }

    public Object[] onPassivate() {
        return catalog == null ? null : new Object[]{catalog.getId()};
    }

    @Cached
    public GenericSelectModel<CatalogEntity> getCatalogModel() {
        if (isAdmin()) return new GenericSelectModel<>(getCatalogService().findAllCatalogs(), CatalogEntity.class, "name", "id", getPropertyAccess());
        else return new GenericSelectModel<>(getCatalogService().findAllCatalogs(getAuthenticatedUser()), CatalogEntity.class, "name", "id", getPropertyAccess());
    }

    public List<BillEntity> getAllBills() {
        return getOrderService().findAllBills(catalog).stream().filter(t -> !t.isCurrentMonth()).collect(Collectors.toList());
    }

    public List<CatalogEntity> getAllCatalogs() {
        return getCatalogService().findAllCatalogs();
    }

    public String getOneBillStatus() {
        return getMessages().get(oneBill.getStatus().name());
    }

    public List<OrderEntity> getOrders() {
        return oneBill.getOrders().stream().filter(OrderEntity::isClosed).collect(Collectors.toList());
    }

    public String getOneOrderStatus() {
        return getMessages().get(oneOrder.getStatus().toString());
    }

}
