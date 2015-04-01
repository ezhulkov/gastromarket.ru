package org.ohm.gastro.gui.pages.catalog;

import org.apache.commons.lang.ObjectUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.RatingEntity;
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
    private RatingEntity oneRating;

    @Property
    private String rateComment;

    @Inject
    @Property
    private Block productsBlock;

    @Inject
    @Property
    private Block catalogFormBlock;

    @Component(id = "name", parameters = {"value=catalog?.name", "validate=maxlength=64,required"})
    private TextField nameField;

    @Component(id = "desc", parameters = {"value=catalog?.description", "validate=maxlength=512,required"})
    private TextArea descField;

    @Component(id = "delivery", parameters = {"value=catalog?.delivery", "validate=maxlength=512,required"})
    private TextArea deliveryField;

    @Component(id = "payment", parameters = {"value=catalog?.payment", "validate=maxlength=256"})
    private TextArea pmtField;

    @Component(id = "basketMin", parameters = {"value=catalog?.basketMin"})
    private TextField bmField;

    public Block onFailureFromCatalogForm() {
        return catalogFormBlock;
    }

    public Block onSuccessFromCatalogForm() {
        getCatalogService().saveCatalog(catalog);
        getCatalogService().setupCatalog(catalog);
        closeImmediately = true;
        return catalogFormBlock;
    }

    public Object onActivate(String pid) {
        catalog = getCatalogService().findCatalog(pid);
        if (catalog == null) return new HttpError(404, "Page not found.");
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
    public java.util.List<RatingEntity> getRatings() {
        return getCatalogService().findAllRatings(catalog);
    }

    @Cached
    public java.util.List<ProductEntity> getProducts() {
        final java.util.List<ProductEntity> allProducts = getProductService().findAllProducts(null, catalog, false);
        return allProducts.stream().limit(allProducts.size() < 8 ? 4 : 8).collect(Collectors.toList());
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

    public String getOneRatingComment() {
        String text = (String) ObjectUtils.defaultIfNull(oneRating.getComment(), "");
        text = text.replaceAll("\\n", "<br/>");
        return text;
    }

    @Cached
    public boolean isCatalogOwner() {
        return catalog.getUser().equals(getAuthenticatedUserOpt().orElse(null));
    }

    @Cached
    public boolean isCommentAllowed() {
        return getAuthenticatedUserOpt().map(t -> getOrderService().findAllOrders(t, catalog).size()).orElse(0) > 0;
    }

    public void onSuccessFromRateForm() {
        getCatalogService().rateCatalog(catalog, rateComment, 5, getAuthenticatedUserOpt().orElse(null));
    }

    public String getProductsCount() {
        return getDeclInfo("products", getProductService().findProductsForFrontendCount(catalog));
    }

    public String getRatingCount() {
        return getDeclInfo("ratings", getRatings().size());
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

}
