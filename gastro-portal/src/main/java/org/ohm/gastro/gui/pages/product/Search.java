package org.ohm.gastro.gui.pages.product;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.ScrollableProducts;

import java.io.IOException;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Search extends ScrollableProducts {

    @Property
    private String searchString = "";

    @Property
    private ProductEntity oneProduct;

    public boolean onActivate() {
        initScrollableContext(null, null, null, null);
        return true;
    }

    public boolean onActivate(String searchString) {
        initScrollableContext(null, null, null, null);
        this.searchString = searchString;
        return true;
    }

    public String onPassivate() {
        return searchString;
    }

    public String getTitle() {
        return getMessages().get("search.title");
    }

    @Override
    public java.util.List<ProductEntity> getProducts() {
        return getProductService().searchProducts(searchString, from, to);
    }

    public static String processSearchString(String searchString) {
        if (searchString == null) return "";
        searchString = searchString.replaceAll(":", "");
        searchString = searchString.replaceAll(";", "");
        searchString = searchString.replaceAll("<", "");
        searchString = searchString.replaceAll(">", "");
        return searchString;
    }

    public Link onSubmitFromSearchForm() throws IOException {
        return getPageLinkSource().createPageRenderLinkWithContext(Search.class, Search.processSearchString(searchString));
    }

}
