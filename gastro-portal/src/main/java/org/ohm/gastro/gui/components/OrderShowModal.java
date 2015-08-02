package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

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
        return getShoppingCart().getItems().get(getShoppingCart().getItems().size() - 1).getEntity().getCatalog();
    }

    public boolean isNewOrder() {
        return getShoppingCart().getItems(getCatalog()).size() == 1;
    }

    public boolean isManyCatalogs() {
        return getShoppingCart().getCatalogs().size() > 1;
    }

}
