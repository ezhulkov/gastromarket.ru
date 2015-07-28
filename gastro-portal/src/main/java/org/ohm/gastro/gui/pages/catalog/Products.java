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
import org.ohm.gastro.service.ProductService;
import org.ohm.gastro.service.ProductService.OrderType;
import org.springframework.data.domain.Sort.Direction;

import java.util.Arrays;
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

    @Inject
    @Property
    private Block productsBlock;

    @Property
    private CatalogEntity catalog;

    @Property
    private String reorder;

    @Property
    private PropertyValueEntity propertyValue = null;

    @Property
    private PropertyValueEntity parentPropertyValue = null;

    @Property
    private ProductService.OrderType orderType = OrderType.NONE;

    @Property
    protected Direction direction = null;

    public boolean onActivate(String catId) {
        return onActivate(catId, null);
    }

    public boolean onActivate(String catId, String ppid) {
        return onActivate(catId, null, ppid, OrderType.POSITION, null);
    }

    public boolean onActivate(String catId, String ppid, String pid) {
        return onActivate(catId, ppid, pid, OrderType.POSITION, null);
    }

    public boolean onActivate(String catId, String ppid, String pid, ProductService.OrderType orderType, Direction direction) {
        this.catalog = getCatalogService().findCatalog(catId);
        this.parentPropertyValue = ppid == null ? null : getPropertyService().findPropertyValue(ppid);
        this.propertyValue = pid == null ? null : getPropertyService().findPropertyValue(pid);
        this.direction = direction;
        this.orderType = orderType;
        return true;
    }

    public Object[] onPassivate() {
        return new Object[]{
                catalog.getId(),
                propertyValue == null ? null : propertyValue.getAltId(),
                parentPropertyValue == null ? null : parentPropertyValue.getAltId(),
                orderType == null ? null : orderType.name().toLowerCase(),
                direction == null ? null : direction.name().toLowerCase()};
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
                getProductService().findAllProducts(propertyValue, catalog) :
                getProductService().findProductsForFrontend(propertyValue, catalog, OrderType.POSITION, Direction.ASC, 0, Integer.MAX_VALUE);
    }

    public java.util.List<PropertyValueEntity> getAllProperties() {
        return getProductService().findProductsForFrontend(null, catalog, null, null, 0, Integer.MAX_VALUE).stream()
                .flatMap(t -> t.getValues().stream())
                .map(TagEntity::getValue)
                .filter(Objects::nonNull)
                .filter(t -> t.getTag() == Tag.ROOT)
                .flatMap(t -> Stream.concat(Stream.of(t), t.getParents().stream()))
                .distinct()
                .collect(Collectors.toList());
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

    public String getSortable() {
        return isCatalogOwner() ? "sortable-container" : "";
    }

    public Block onActionFromReorderForm() {
        getProductService().productPosition(Arrays.stream(reorder.split(",")).map(Long::parseLong).collect(Collectors.toList()));
        return productsBlock;
    }

}
