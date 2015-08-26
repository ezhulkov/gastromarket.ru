package org.ohm.gastro.gui.pages.admin.order;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Select;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.misc.GenericSelectModel;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class List extends BaseComponent {

    @Property
    private OrderEntity oneOrder;

    @Property
    private CatalogEntity catalog;

    @Component(id = "catalog", parameters = {"model=catalogModel", "encoder=catalogModel", "value=catalog"})
    private Select pCatalogField;

    public boolean onActivate() {
        catalog = null;
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
        return new GenericSelectModel<>(getCatalogService().findAllCatalogs(), CatalogEntity.class, "name", "id", getPropertyAccess());
    }

    public java.util.List<OrderEntity> getAllOrders() {
        return getOrderService().findAllOrders(catalog);
    }

    public String getOneOrderStatus() {
        return getMessages().get(oneOrder.getStatus().toString());
    }

}
