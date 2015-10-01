package org.ohm.gastro.gui.pages.catalog;

import com.google.common.collect.Lists;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.gui.dto.NewProducts;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.ProductService;
import org.ohm.gastro.service.ProductService.OrderType;
import org.springframework.data.domain.Sort.Direction;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Products extends BaseComponent {

    private final static int PAGE_SIZE = 28;

    @SessionState
    private NewProducts newProducts;

    @Property
    private ProductEntity editedProduct;

    @Property
    private ProductEntity oneProduct;

    @Inject
    @Property
    private Block newProductsBlock;

    @Property
    private int fetchFrom = 0;

    @Property
    private int fetchTo = PAGE_SIZE;

    @Inject
    @Property
    private Block productsFetchBlock;

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
    private ProductService.OrderType orderType = OrderType.POSITION;

    @Property
    protected Direction direction = null;

    public void setupRender() {
        newProducts.getItems().clear();
    }

    public Object onActivate() {
        return onActivate(null);
    }

    public Object onActivate(String catId) {
        return onActivate(catId, null);
    }

    public Object onActivate(String catId, String ppid) {
        return onActivate(catId, null, ppid, OrderType.POSITION, null);
    }

    public Object onActivate(String catId, String ppid, String pid) {
        return onActivate(catId, ppid, pid, OrderType.POSITION, null);
    }

    public Object onActivate(String catId, String ppid, String pid, ProductService.OrderType orderType, Direction direction) {
        this.catalog = getCatalogService().findCatalog(catId);
        if (catalog == null) return new HttpError(404, "Page not found.");
        this.parentPropertyValue = ppid == null ? null : getPropertyService().findPropertyValue(ppid);
        this.propertyValue = pid == null ? null : getPropertyService().findPropertyValue(pid);
        this.direction = direction;
        this.orderType = orderType;
        return true;
    }

    public Object[] onPassivate() {
        return catalog == null ? null :
                new Object[]{
                        catalog.getId(),
                        parentPropertyValue == null ? null : parentPropertyValue.getAltId(),
                        propertyValue == null ? null : propertyValue.getAltId(),
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

    public java.util.List<ProductEntity> getNewProducts() {
        return newProducts.getItems();
    }

    public java.util.List<ProductEntity> getProducts() {
        return propertyValue.getId() == null ?
                (isCatalogOwner() ? getProductService().findAllRawProducts(catalog, fetchFrom, fetchTo) : null) :
                getProductService().findProductsForFrontend(propertyValue, catalog,
                                                            isCatalogOwner() ? null : true,
                                                            isCatalogOwner() ? null : false,
                                                            orderType, direction, propertyValue.getId().toString(), fetchFrom, fetchTo);
    }

    public boolean isShowFetch() {
        return isCatalogOwner() && getProductService().findAllCategoryProductsCount(catalog, propertyValue) > fetchTo;
    }

    public void onActionFromFetchProductsAjaxLink(Long cid, int from, int to) {
        this.fetchFrom = from;
        this.fetchTo = to;
        this.propertyValue = cid == null ? new PropertyValueEntity() : getPropertyService().findPropertyValue(cid);
        getAjaxResponseRenderer()
                .addRender(getProductsZoneId(), productsBlock)
                .addRender(getProductsFetchZoneId(), productsFetchBlock);
        this.fetchFrom += to - from;
        this.fetchTo += PAGE_SIZE;
    }

    public String getProductsZoneId() {
        return String.format("productsZone_%s_%s_%s", propertyValue.getId(), fetchFrom, fetchTo);
    }

    public String getProductsFetchZoneId() {
        return String.format("productsFetchZone_%s", propertyValue.getId());
    }

    public String getCategoryName() {
        return propertyValue.getId() == null ? getMessages().get("raw.products") : propertyValue.getName();
    }

    public String getCategoryTip() {
        return propertyValue.getId() == null ? getMessages().get("raw.products.tip") : null;
    }

    public java.util.List<PropertyValueEntity> getAllProperties() {
        return getProductService().findAllRootValues(catalog, isCatalogOwner() ? null : true).stream()
                .flatMap(t -> Stream.concat(Stream.of(t), t.getParents().stream()))
                .distinct()
                .collect(Collectors.toList());
    }

    @Cached
    public java.util.List<PropertyValueEntity> getRootProperties() {
        if (propertyValue != null) return Lists.newArrayList(propertyValue);
        final java.util.List<PropertyValueEntity> categories = getProductService().findAllRootValues(catalog, isCatalogOwner() ? null : true).stream()
                .flatMap(t -> t.getParents().isEmpty() ? Stream.of(t) : t.getParents().stream())
                .distinct()
                .collect(Collectors.toList());
        if (isCatalogOwner() && getProductService().findAllCategoryProductsCount(catalog, new PropertyValueEntity()) > 0) categories.add(0, new PropertyValueEntity());
        return categories;
    }

    public String getSortable() {
        return isCatalogOwner() && propertyValue.getId() != null ? "sortable-container" : "";
    }

}
