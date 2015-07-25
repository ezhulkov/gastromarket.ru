package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.PropertyValueEntity.Tag;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Products extends BaseComponent {

    @Property
    private ProductEntity editedProduct;

    @Property
    private ProductEntity oneProduct;

    @Property
    private PropertyValueEntity rootProperty;

    @Inject
    @Property
    private Block productsBlock;

    @Property
    private CatalogEntity catalog;

    public void onActivate(String catId) {
        this.catalog = getCatalogService().findCatalog(catId);
    }

    public Object[] onPassivate() {
        return new Object[]{catalog.getId()};
    }

    public String getTitle() {
        return catalog.getName();
    }

    @Cached
    public boolean isCatalogOwner() {
        return catalog.getUser().equals(getAuthenticatedUserOpt().orElse(null));
    }

    public java.util.List<ProductEntity> getProducts() {
        return isCatalogOwner() ?
                getProductService().findAllProducts(rootProperty, catalog) :
                getProductService().findProductsForFrontend(rootProperty, catalog, null, null, 0, Integer.MAX_VALUE);
    }

    public java.util.List<PropertyValueEntity> getRootProperties() {
        return getProductService().findProductsForFrontend(null, catalog, null, null, 0, Integer.MAX_VALUE).stream()
                .flatMap(t -> t.getValues().stream())
                .map(TagEntity::getValue)
                .filter(Objects::nonNull)
                .filter(t -> t.getTag() == Tag.ROOT)
                .flatMap(t -> t.getParents().isEmpty() ? Stream.of(t) : t.getParents().stream())
                .distinct()
                .collect(Collectors.toList());
    }

}
