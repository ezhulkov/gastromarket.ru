package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.domain.PurchaseEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class OrderShowCatalog extends BaseComponent {

    @Parameter
    @Property
    private CatalogEntity catalog;

    @Property
    private OrderProductEntity item;

    @Property
    @Inject
    private Block orderShowBlock;

    public boolean isNewOrder() {
        return getItems().size() == 1;
    }

    public List<OrderProductEntity> getItems() {
        return getShoppingCart().getItems(catalog);
    }

    public void onActionFromDelete(PurchaseEntity.Type type, Long id) {
        getShoppingCart().removeItem(type, id);
    }

}
