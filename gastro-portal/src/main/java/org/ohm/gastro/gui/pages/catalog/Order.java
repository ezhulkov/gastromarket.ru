package org.ohm.gastro.gui.pages.catalog;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.AltIdBaseEntity;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.components.comment.InjectPhotos;
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

    @Component(id = "comment", parameters = {"value=order.comment"})
    private TextArea comment;

    @Component(id = "mobile", parameters = {"value=mobile", "validate=required"})
    private TextField mobileField;

    @Component(id = "address", parameters = {"value=authenticatedUser.deliveryAddress", "validate=required"})
    private TextArea address;

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

    @Property
    private java.util.List<ProductEntity> cartProducts;

    @InjectComponent
    private InjectPhotos injectPhotos;

    @Persist
    @Property
    private java.util.List<PhotoEntity> photos;

    public String getBudgetTitle() {
        int total = cartProducts.stream().mapToInt(ProductEntity::getPrice).sum();
        return total == 0 ? getMessages().get("direct.budget") : getMessages().format("direct.budget.n", total);
    }

    public Object onActivate(String altId) {
        catalog = getCatalogService().findCatalog(altId);
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
        if (photos == null) photos = Lists.newArrayList();
        if (order == null || order.getId() != null) order = new OrderEntity();
        if (mobile == null) mobile = getAuthenticatedUserOpt().map(UserEntity::getMobilePhone).orElse(null);
        cartProducts = getShoppingCart().getItems(catalog).stream()
                .filter(t -> t.getEntity() instanceof ProductEntity)
                .map(t -> (ProductEntity) t.getEntity())
                .collect(Collectors.toList());
        cartProducts.forEach(p -> {
            boolean added = photos.stream().filter(t -> t.getProduct() != null && t.getProduct().equals(p)).findAny().isPresent();
            if (!added) {
                final PhotoEntity productPhoto = new PhotoEntity();
                productPhoto.setProduct(p);
                getPhotoService().savePhoto(productPhoto);
                photos.add(productPhoto);
            }
        });
        order.setName(cartProducts.stream().findFirst().map(AltIdBaseEntity::getName).orElse(""));
    }

    public Object onSubmitFromOrderInfoForm() {
        photos = injectPhotos.getSubmittedPhotos();
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
        order.setTotalPrice(null);
        if (StringUtils.isNotEmpty(budget)) {
            budget = budget.replaceAll("\\.", "").replaceAll(",", "");
            if (StringUtils.isNotEmpty(budget)) {
                int i = 0;
                try {
                    i = Integer.parseInt(budget);
                } catch (NumberFormatException e) {
                    logger.error(budget + " " + e.getMessage());
                }
                if (i > 0) order.setTotalPrice(i);
            }
        }
        getOrderService().placeOrder(order, photos, getAuthenticatedUser(), catalog);
        photos.clear();
        injectPhotos.purgeSubmittedPhotos();
        return getPageLinkSource().createPageRenderLinkWithContext(OrderResults.class, order.getId());
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
