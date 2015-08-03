package org.ohm.gastro.gui.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PurchaseEntity;
import org.ohm.gastro.domain.PurchaseEntity.Type;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class OrderShowCatalog extends BaseComponent {

    @Parameter
    @Property
    private CatalogEntity catalog;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    @Property
    private String additionalClass;

    @Property
    private OrderProductEntity item;

    @Property
    @Inject
    private Block orderShowBlock;

    @Property
    private int index;

    public String getItemIndex() {
        return String.format("%s.", index + 1);
    }

    public boolean isNewOrder() {
        return getItems().size() == 1;
    }

    public List<OrderProductEntity> getItems() {
        return getShoppingCart().getItems(catalog);
    }

    public void onActionFromDelete(PurchaseEntity.Type type, Long id, Long mid) {
        getShoppingCart().removeItem(type, id, mid);
    }

    public String getProductUnit() {
        if (item.getEntity().getType() == Type.PRODUCT) return getMessages().format(((ProductEntity) item.getEntity()).getUnit().name() + "_TEXT",
                                                                                    ((ProductEntity) item.getEntity()).getUnitValue()).toLowerCase();
        return null;
    }

    public int getTotal() {
        return getShoppingCart().getCatalogPrice(catalog);
    }

    public String getRowClass() {
        return index % 2 == 0 ? "odd" : "even";
    }

}
