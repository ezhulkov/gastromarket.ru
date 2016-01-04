package org.ohm.gastro.gui.components;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.product.Search;

/**
 * Created by ezhulkov on 30.05.15.
 */
public class SearchSection extends BaseComponent {

    @Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String additionalClass;

    @Property
    @Parameter(defaultPrefix = BindingConstants.PROP)
    private String searchString;

    public Object onSubmitFromSearchAjaxForm() {
        System.out.println(searchString);
        if (StringUtils.isNotEmpty(searchString)) {
            return getPageLinkSource().createPageRenderLinkWithContext(Search.class, processSearchString(searchString));
        } else {
            return this;
        }
    }

    private String processSearchString(String searchString) {
        if (searchString == null) return "";
        searchString = searchString.replaceAll(":", "");
        searchString = searchString.replaceAll(";", "");
        searchString = searchString.replaceAll("<", "");
        searchString = searchString.replaceAll(">", "");
        searchString = searchString.replaceAll("\\|", "");
        searchString = searchString.replaceAll("drop", "");
        searchString = searchString.replaceAll("delete", "");
        searchString = searchString.replaceAll("truncate", "");
        return searchString;
    }

}
