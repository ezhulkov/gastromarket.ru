package org.ohm.gastro.gui.pages.catalog;

import org.apache.commons.lang.ObjectUtils;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.PropertyValueEntity.Tag;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class List extends BaseComponent {

    @Property
    private CatalogEntity oneCatalog;

    @Property
    private ProductEntity oneProduct;

    @Property
    private PropertyEntity oneProperty;

    public Object onActivate(EventContext context) {
        if (context.getCount() == 0) return null;
        return new HttpError(404, "Page not found.");
    }

    @Cached
    public java.util.List<CatalogEntity> getCatalogs() {
        return getCatalogService().findAllActiveCatalogs();
    }

    public java.util.List<ProductEntity> getProducts() {
        return getProductService().findProductsForFrontend(null, oneCatalog, null, null, 0, 3);
    }

    public String getRootProperties() {
        return getProductService().findProductsForFrontend(null, oneCatalog, null, null, 0, Integer.MAX_VALUE).stream()
                .flatMap(t -> t.getValues().stream())
                .map(TagEntity::getValue)
                .filter(t -> t.getTag() == Tag.ROOT)
                .map(PropertyValueEntity::getValue)
                .collect(Collectors.joining(", "));
    }

    public boolean getHasRatings() {
        return getRatingService().findAllComments(oneCatalog).stream().filter(t -> t.getRating() != 0).findAny().isPresent();
    }

    public long getPosRatings() {
        return getRatingService().findAllComments(oneCatalog).stream().filter(t -> t.getRating() > 0).count();
    }

    public long getNegRatings() {
        return getRatingService().findAllComments(oneCatalog).stream().filter(t -> t.getRating() < 0).count();
    }

    @Cached
    public String getDescription() {
        String desc = (String) ObjectUtils.defaultIfNull(oneCatalog.getDescription(), "");
        desc = desc.replaceAll("\\n", "<br/>");
        return desc;
    }

}
