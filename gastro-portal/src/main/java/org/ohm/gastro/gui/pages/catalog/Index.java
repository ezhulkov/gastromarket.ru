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
import org.ohm.gastro.domain.PropertyValueEntity.Tag;
import org.ohm.gastro.domain.TagEntity;

import java.util.Objects;
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
    private CommentEntity oneComment;

    @Property
    private OfferEntity offer;

    @Property
    private String rateComment;

    @Property
    private boolean opinion = true;

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
        final java.util.List<ProductEntity> allProducts = isCatalogOwner() ?
                getProductService().findAllProducts(null, catalog) :
                getProductService().findProductsForFrontend(null, catalog, null, null, 0, Integer.MAX_VALUE);
        return allProducts.stream().limit(4).collect(Collectors.toList());
    }

    public String getOneCommentText() {
        String text = (String) ObjectUtils.defaultIfNull(oneComment.getText(), "");
        text = text.replaceAll("\\n", "<br/>");
        return text;
    }

    @Cached
    public boolean isCommentAllowed() {
        return getAuthenticatedUserOpt()
                .filter(t -> t.isUser() || t.isAdmin())
                .map(t -> getOrderService().findAllOrders(t, catalog).size())
                .orElse(0) > 0;
    }

    public void onSuccessFromRateForm() {
        getRatingService().rateCatalog(catalog, rateComment, opinion ? 1 : -1, getAuthenticatedUserOpt().orElse(null));
    }

    public String getProductsCount() {
        return getDeclInfo("products", getProductService().findProductsForFrontendCount(catalog));
    }

    public String getOrderCount() {
        return getDeclInfo("orders", getOrderService().findAllOrders(catalog, Status.READY).size());
    }

    public String getRootProperties() {
        return getProductService().findProductsForFrontend(null, catalog, null, null, 0, Integer.MAX_VALUE).stream()
                .flatMap(t -> t.getValues().stream())
                .map(TagEntity::getValue)
                .filter(Objects::nonNull)
                .filter(t -> t.getTag() == Tag.ROOT)
                .map(PropertyValueEntity::getName)
                .distinct()
                .collect(Collectors.joining(", "));
    }

    public boolean getLike() {
        return true;
    }

    public boolean getDislike() {
        return false;
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

}
