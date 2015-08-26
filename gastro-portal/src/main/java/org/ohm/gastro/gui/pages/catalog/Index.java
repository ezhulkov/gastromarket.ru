package org.ohm.gastro.gui.pages.catalog;

import org.apache.commons.lang.ObjectUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OfferEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyValueEntity;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Index extends AbstractCatalogPage {

    @Property
    private boolean closeImmediately = false;

    @Property
    private ProductEntity editedProduct;

    @Property
    private String reorder;

    @Property
    private CommentEntity oneComment;

    @Property
    private OfferEntity offer;

    @Inject
    @Property
    private Block catalogFormBlock;

    public Object onActivate(String pid) {
        catalog = getCatalogService().findCatalog(pid);
        if (catalog == null) return new HttpError(404, "Page not found.");
        if (!catalog.isWasSetup() && isAuthenticated()) return getPageLinkSource().createPageRenderLinkWithContext(Wizard.class, catalog.getId());
        return true;
    }

    public Object[] onPassivate() {
        return new Object[]{catalog.getAltId()};
    }

    @Cached
    public java.util.List<CommentEntity> getComments() {
        return getRatingService().findAllComments(catalog);
    }

    @Cached
    public java.util.List<ProductEntity> getProducts() {
        final java.util.List<ProductEntity> allProducts = getProductService().findProductsForFrontend(null, catalog, isCatalogOwner() ? null : true, null, null, null, 0, Integer.MAX_VALUE);
        return allProducts.stream().limit(4).sorted((o1, o2) -> ObjectUtils.compare(o1.getPositionOfType("main"), o2.getPositionOfType("main"))).collect(Collectors.toList());
    }

    @Cached
    public boolean isCommentAllowed() {
        return getAuthenticatedUserOpt()
                .filter(t -> t.isUser() || t.isAdmin())
                .map(t -> getOrderService().findAllOrders(t, catalog).size())
                .orElse(0) > 0;
    }

    public String getProductsCount() {
        return getDeclInfo("products", getProductService().findProductsForFrontendCount(catalog));
    }

    public String getOrderCount() {
        return getDeclInfo("orders", getOrderService().findAllOrders(catalog, Status.CLOSED).size());
    }

    public String getRootProperties() {
        return getProductService().findAllRootValues(catalog, isCatalogOwner() ? null : true).stream()
                .map(PropertyValueEntity::getName)
                .distinct()
                .collect(Collectors.joining(", "));
    }

    public String getMedActiveClass() {
        return "inactive";
    }

    public String getAddActiveClass() {
        return "inactive";
    }

    public String getZakActiveClass() {
        return "inactive";
    }

    public Class onSuccessFromCatalogForm() {
        getCatalogService().saveCatalog(catalog);
        return Index.class;
    }

    @Cached
    public java.util.List<OfferEntity> getOffers() {
        return getOfferService().findAllOffers(catalog).stream().limit(3).collect(Collectors.toList());
    }

    @Override
    public String getDescLabel() {
        return getMessages().get("desc.label.nologo." + catalog.getType().name().toLowerCase());
    }

    public String getBasketMinText() {
        return getMessages().format("basket.min.text", catalog.getBasketMin());
    }

    public String getPrepaymentText() {
        return getMessages().format("prepayment.text2", catalog.getPrepayment());
    }

    public Block onActionFromReorderForm() {
        getProductService().productPosition(Arrays.stream(reorder.split(",")).map(Long::parseLong).collect(Collectors.toList()), "main");
        return productsBlock;
    }
}
