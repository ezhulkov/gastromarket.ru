package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PurchaseEntity;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class Items extends AbstractOrder {

    @Property
    private OrderProductEntity item;

    @Property
    private int index;

    public String getProductUnit() {
        if (item.getEntity().getType() == PurchaseEntity.Type.PRODUCT) return getMessages().format(((ProductEntity) item.getEntity()).getUnit().name() + "_TEXT",
                                                                                                   ((ProductEntity) item.getEntity()).getUnitValue()).toLowerCase();
        return null;
    }

    public Object[] getDeleteContext() {
        return new Object[]{
                catalog == null ? null : catalog.getId(),
                order == null ? null : order.getId(),
                item.getId(),
                item.getEntity().getType(),
                item.getEntity().getId(),
                item.getModifier() == null ? null : item.getModifier().getId()
        };
    }

    public String getItemPage() {
        if (item.getEntity().getType() == PurchaseEntity.Type.PRODUCT) return "/product/" + item.getEntity().getAltId();
        return "/catalog/offer/" + item.getEntity().getAltId();
    }

    public Block onActionFromDeleteItem(Long cId, Long oId, Long opId, PurchaseEntity.Type type, Long id, Long mid) {
        this.catalog = getCatalogService().findCatalog(cId);
        this.order = getOrderService().findOrder(oId);
        if (order == null) {
            getShoppingCart().removeItem(type, id, mid);
        } else {
            getOrderService().deleteProduct(oId, opId, getAuthenticatedUser());
        }
        return orderBlock;
    }

    public String getItemIndex() {
        return String.format("%s.", index + 1);
    }

    public String getRowClass() {
        return index % 2 == 0 ? "odd" : "even";
    }

}
