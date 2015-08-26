package org.ohm.gastro.gui.components.order;

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
public class ViewModal extends BaseComponent {

    @Property
    private OrderProductEntity item;

    @Property
    private CatalogEntity catalog;

    @Property
    @Inject
    private Block orderShowBlock;

    public void beginRender() {
        catalog = getShoppingCart().getLastItem().getEntity().getCatalog();
    }

    public boolean isNewOrder() {
        List<OrderProductEntity> items = getShoppingCart().getItems(catalog);
        return items.size() == 1 && items.stream().allMatch(t -> t.getCount() == 1);
    }

    public boolean isManyCatalogs() {
        return getShoppingCart().getCatalogs().size() > 1;
    }

}
