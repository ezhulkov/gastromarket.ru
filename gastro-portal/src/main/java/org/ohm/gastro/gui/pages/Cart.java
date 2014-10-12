package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Cart extends BaseComponent {

    @Property
    private ProductEntity oneProduct;

    @Property
    private Map.Entry<CatalogEntity, List<ProductEntity>> oneCatalog;

    public void onActionFromDeleteProduct(Long pid) {
        getShoppingCart().removeProduct(new ProductEntity(pid));
    }

    public List<Map.Entry<CatalogEntity, List<ProductEntity>>> getCatalogs() {
        return getShoppingCart().getProducts().stream()
                .map(t -> getCatalogService().findProduct(t.getId()))
                .collect(Collectors.groupingBy(ProductEntity::getCatalog)).entrySet().stream()
                .collect(Collectors.toList());
    }

    public Integer getTotalPrice() {
        return getShoppingCart().getProducts().stream().collect(Collectors.summingInt(ProductEntity::getPrice));
    }

}
