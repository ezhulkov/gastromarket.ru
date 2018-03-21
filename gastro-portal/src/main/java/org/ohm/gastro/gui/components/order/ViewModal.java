package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class ViewModal extends BaseComponent {

    @Property
    private OrderProductEntity item;

    public CatalogEntity getCatalog() {
        return getShoppingCart().getLastItem().getEntity().getCatalog();
    }

    public boolean isManyCatalogs() {
        return getShoppingCart().getCatalogs().size() > 1;
    }

}
