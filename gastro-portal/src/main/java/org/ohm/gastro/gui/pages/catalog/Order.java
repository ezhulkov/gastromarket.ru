package org.ohm.gastro.gui.pages.catalog;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.dto.Breadcrumb;
import org.ohm.gastro.gui.pages.AbstractPage;

import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 09.03.16.
 */
public class Order extends AbstractPage {

    @Persist
    @Property
    private OrderEntity order;

    @Property
    private OrderEntity oneSample;

    @Property
    @Persist
    private String mobile;

    @Component(id = "orderInfoForm")
    private Form orderInfoForm;

    @Property
    private ProductEntity oneProduct;

    @Component(id = "name", parameters = {"value=order.name", "validate=required"})
    private TextField name;

    @Component(id = "comment", parameters = {"value=order.comment", "validate=required"})
    private TextArea comment;

    @Component(id = "mobile", parameters = {"value=mobile", "validate=required"})
    private TextField mobileField;

    @Component(id = "dueDate", parameters = {"value=order.dueDateAsString", "validate=required"})
    private TextField dueDate;

    @Component(id = "promoCode", parameters = {"value=order.promoCode"})
    private TextField promoCode;

    @Component(id = "budget", parameters = {"value=budget", "validate=regexp"})
    private TextField budgetField;

    @Property
    private String budget;

    @Inject
    @Property
    private Block samplesBlock;

    @Inject
    @Property
    private Block orderInfoBlock;

    @Property
    private boolean error = false;

    @Property
    private boolean needLogin = false;

    @Property
    private CatalogEntity catalog;

    public Object onActivate(String altId) {
        this.catalog = getCatalogService().findCatalog(altId);
        if (catalog == null) return new HttpError(404, "Page not found.");
        return null;
    }

    public String onPassivate() {
        return catalog == null ? null : catalog.getAltId();
    }

    public java.util.List<OrderEntity> getSamples() {
        return getOrderService().findAllOrders(catalog).stream().filter(t -> StringUtils.isNotEmpty(t.getName())).limit(5).collect(Collectors.toList());
    }

    public void onPrepareFromOrderInfoForm() {
        if (order == null) order = new OrderEntity();
        if (mobile == null) mobile = getAuthenticatedUserOpt().map(UserEntity::getMobilePhone).orElse(null);
    }

    public Object onSubmitFromOrderInfoForm() {
        if (orderInfoForm.getHasErrors() || mobile == null || order.getDueDate() == null) {
            error = true;
            return orderInfoBlock;
        }
        if (!isAuthenticated()) {
            needLogin = true;
            return orderInfoBlock;
        }
        if (!mobile.equals(getAuthenticatedUser().getMobilePhone())) {
            getAuthenticatedUser().setMobilePhone(mobile);
            getUserService().saveUser(getAuthenticatedUser());
        }
//        tender.setTotalPrice(null);
//        if (StringUtils.isNotEmpty(budget)) {
//            budget = budget.replaceAll("\\.", "").replaceAll(",", "");
//            if (StringUtils.isNotEmpty(budget)) {
//                final int i = Integer.parseInt(budget);
//                if (i > 0) tender.setTotalPrice(i);
//            }
//        }
//        tender.setCustomer(getAuthenticatedUser());
//        final OrderEntity newTender = getOrderService().saveOrder(tender);
//        java.util.List<PhotoEntity> photos = getTenderPhotos();
//        photos = photos.stream().peek(t -> t.setId(null)).collect(Collectors.toList());
//        getPhotoService().attachPhotos(newTender, photos);
//        getOrderService().placeTender(newTender, getAuthenticatedUser());
//        return getPageLinkSource().createPageRenderLink(AddResults.class);
        return null;
    }

    @Override
    public java.util.List<Breadcrumb> getBreadcrumbsContext() {
        return Lists.newArrayList(
                mainPage,
                Breadcrumb.of(getMessages().get(List.class.getName()), List.class),
                Breadcrumb.of(catalog.getName(), Index.class, catalog.getAltId()),
                Breadcrumb.of(getMessages().get(Order.class.getName()), Order.class, catalog.getAltId())
        );
    }

    public java.util.List<ProductEntity> getProducts() {
        return getProductService().findProductsForFrontend(null, catalog, true, false, null, null, null, null, 0, 40);
    }

    public java.util.List<ProductEntity> getAllProducts() {
        return getProductService().findProductsForFrontend(null, catalog, true, false, null, null, null, null, 0, Integer.MAX_VALUE);
    }

}
