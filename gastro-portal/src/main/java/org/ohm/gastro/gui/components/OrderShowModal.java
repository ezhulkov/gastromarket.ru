package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class OrderShowModal extends BaseComponent {

    @Property
    private OrderProductEntity item;

    @Property
    @Inject
    private Block orderShowBlock;

    public CatalogEntity getCatalog() {
        return getShoppingCart().getLastItem().getEntity().getCatalog();
    }

    public boolean isNewOrder() {
        List<OrderProductEntity> items = getShoppingCart().getItems(getCatalog());
        return items.size() == 1 && items.stream().allMatch(t -> t.getCount() == 1);
    }

    public boolean isManyCatalogs() {
        return getShoppingCart().getCatalogs().size() > 1;
    }

}
