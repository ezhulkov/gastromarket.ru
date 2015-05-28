package org.ohm.gastro.gui.pages.catalog;

import org.apache.commons.lang.ObjectUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Index extends BaseComponent {

    @Property
    private boolean closeImmediately = false;

    @Property
    private CatalogEntity catalog;

    @Property
    private ProductEntity oneProduct;

    @Property
    private ProductEntity editedProduct;

    @Property
    private CommentEntity oneComment;

    @Property
    private String rateComment;

    @Inject
    @Property
    private Block productsBlock;

    @Property
    private boolean opinion = true;

    @Inject
    @Property
    private Block catalogFormBlock;

    public Object onActivate(String pid) {
        catalog = getCatalogService().findCatalog(pid);
        if (catalog == null) return new HttpError(404, "Page not found.");
        if (!catalog.isWasSetup()) return getPageLinkSource().createPageRenderLinkWithContext(Wizard.class, catalog.getId());
        return true;
    }

    public Object[] onPassivate() {
        return new Object[]{catalog.getAltId()};
    }

    public Consumer<ProductEntity> getProductSetter() {
        return productEntity -> {
            editedProduct = productEntity;
        };
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

    @Cached
    public String getDescription() {
        String desc = (String) ObjectUtils.defaultIfNull(catalog.getDescription(), "");
        desc = desc.replaceAll("\\n", "<br/>");
        return desc;
    }

    @Cached
    public String getDelivery() {
        String desc = (String) ObjectUtils.defaultIfNull(catalog.getDelivery(), "");
        desc = desc.replaceAll("\\n", "<br/>");
        return desc;
    }

    @Cached
    public String getPayment() {
        String desc = (String) ObjectUtils.defaultIfNull(catalog.getPayment(), "");
        desc = desc.replaceAll("\\n", "<br/>");
        return desc;
    }

    public String getOneCommentText() {
        String text = (String) ObjectUtils.defaultIfNull(oneComment.getText(), "");
        text = text.replaceAll("\\n", "<br/>");
        return text;
    }

    @Cached
    public boolean isCatalogOwner() {
        return catalog.getUser().equals(getAuthenticatedUserOpt().orElse(null));
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

    public String getRatingCount() {
        return getDeclInfo("ratings", getComments().size());
    }

    public String getOrderCount() {
        return getDeclInfo("orders", getOrderService().findAllOrders(catalog, Status.READY).size());
    }

    private String getDeclInfo(String value, int count) {
        if (count == 0) return getMessages().format(String.format("chef.info.no.%s", value), count);
        if (count == 1) return getMessages().format(String.format("chef.info.one.%s", value), count);
        if (count % 10 < 5) return getMessages().format(String.format("chef.info.four.%s", value), count);
        return getMessages().format(String.format("chef.info.many.%s", value), count);
    }

    public boolean getLike() {
        return true;
    }

    public boolean getDislike() {
        return false;
    }

}
