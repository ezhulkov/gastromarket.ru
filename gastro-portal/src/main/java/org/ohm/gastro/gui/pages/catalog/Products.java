package org.ohm.gastro.gui.pages.catalog;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.gui.dto.Breadcrumb;
import org.ohm.gastro.gui.dto.NewProducts;
import org.ohm.gastro.gui.pages.AbstractPage;
import org.ohm.gastro.service.ProductService.OrderType;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Products extends AbstractPage {

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
    private PropertyValueEntity categoryPropertyValue = null;

    @Property
    private PropertyValueEntity eventPropertyValue = null;

    @Property
    private PropertyValueEntity propertyValue = null;

    @Property
    private PropertyValueEntity parentPropertyValue = null;

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
        return onActivate(catId, null, ppid);
    }

    public Object onActivate(String catId, String ppid, String cid) {
        return onActivate(catId, ppid, cid, null);
    }

    public Object onActivate(String catId, String ppid, String cid, String eid) {
        this.catalog = getCatalogService().findCatalog(catId);
        if (catalog == null) return new HttpError(404, "Page not found.");
        this.parentPropertyValue = ppid == null ? null : getPropertyService().findPropertyValue(ppid);
        this.categoryPropertyValue = cid == null ? null : getPropertyService().findPropertyValue(cid);
        this.eventPropertyValue = eid == null ? null : getPropertyService().findPropertyValue(eid);
        return true;
    }

    public Object[] onPassivate() {
        return catalog == null ? null :
                new Object[]{
                        catalog.getId(),
                        parentPropertyValue == null ? null : parentPropertyValue.getAltId(),
                        categoryPropertyValue == null ? null : categoryPropertyValue.getAltId(),
                        eventPropertyValue == null ? null : eventPropertyValue.getAltId()
                };
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
                                                            null,
                                                            OrderType.POSITION, null,
                                                            propertyValue.getId().toString(), fetchFrom, fetchTo);
    }

    public boolean isShowFetch() {
        return isCatalogOwner() && getProductService().findAllCategoryProductsCount(catalog, getValue()) > fetchTo;
    }

    public void onActionFromFetchProductsAjaxLink(Long cid, Long eid, int from, int to) {
        this.fetchFrom = from;
        this.fetchTo = to;
        this.categoryPropertyValue = cid == null ? new PropertyValueEntity() : getPropertyService().findPropertyValue(cid);
        this.eventPropertyValue = eid == null ? new PropertyValueEntity() : getPropertyService().findPropertyValue(eid);
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

    @Cached
    public java.util.List<PropertyValueEntity> getRootProperties() {
        if (getValue() != null) return Lists.newArrayList(getValue());
        final java.util.List<PropertyValueEntity> categories = getProductService().findAllRootValues(catalog, isCatalogOwner() ? null : true).stream()
                .flatMap(t -> t.getParents().isEmpty() ? Stream.of(t) : t.getParents().stream())
                .distinct()
                .sorted((o1, o2) -> getProductService().findAllCategoryProductsCount(catalog, o2) - getProductService().findAllCategoryProductsCount(catalog, o1))
                .collect(Collectors.toList());
        if (isCatalogOwner() && getProductService().findAllCategoryProductsCount(catalog, new PropertyValueEntity()) > 0) categories.add(0, new PropertyValueEntity());
        return categories;
    }

    public String getSortable() {
        return isCatalogOwner() && getValue().getId() != null ? "sortable-container" : "";
    }

    private PropertyValueEntity getValue() {
        return ObjectUtils.defaultIfNull(categoryPropertyValue, eventPropertyValue);
    }

    @Override
    public java.util.List<Breadcrumb> getBreadcrumbsContext() {
        return Lists.newArrayList(
                mainPage,
                Breadcrumb.of(getMessages().get(List.class.getName()), List.class),
                Breadcrumb.of(catalog.getName(), Index.class, catalog.getAltId()),
                Breadcrumb.of(getMessages().get(Products.class.getName()), Products.class, catalog.getAltId())
        );
    }

}
