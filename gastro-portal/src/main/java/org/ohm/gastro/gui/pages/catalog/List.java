package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class List extends BaseComponent {

    @Property
    private CatalogEntity oneCatalog;

    @Property
    private ProductEntity oneProduct;

    @Property
    private CategoryEntity oneCategory;

    @Cached
    public java.util.List<CatalogEntity> getCatalogs() {
        return getCatalogService().findAllCatalogs().stream().filter(t -> t.getProducts().size() > 0).collect(Collectors.toList());
    }

    public java.util.List<ProductEntity> getProducts() {
        java.util.List<ProductEntity> products = getProductService().findAllProducts(null, oneCatalog, false).stream().limit(3).collect(Collectors.toList());
        Collections.shuffle(products);
        return products;
    }

    public String getCategories() {
        return getProductService().findAllProducts(null, oneCatalog, false).stream()
                .map(ProductEntity::getCategory)
                .map(t -> t.getParent() == null ? t : t.getParent())
                .distinct()
                .sorted((o1, o2) -> o1.getId().compareTo(o2.getId()))
                .map(CategoryEntity::getName)
                .collect(Collectors.joining(", "));
    }

}
