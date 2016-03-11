package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class DirectOrder extends BaseComponent {

    @Parameter
    @Property
    private CatalogEntity catalog;

    @Property
    private ProductEntity product;

    public java.util.List<ProductEntity> getProducts() {
        return getShoppingCart().getItems(catalog).stream()
                .filter(t -> t.getEntity() instanceof ProductEntity)
                .map(t -> (ProductEntity) t.getEntity())
                .collect(Collectors.toList());
    }

    public int getTotal() {
        return getProducts().stream().mapToInt(ProductEntity::getPrice).sum();
    }

}
