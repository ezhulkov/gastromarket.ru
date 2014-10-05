package org.ohm.gastro.gui.pages;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.gui.components.EditObject.FormEvent;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.catalog.Search;

import java.util.List;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Index extends BaseComponent {

    @Property
    private String searchString;

    @Property
    private CategoryEntity oneCategory;

    @Property
    private CategoryEntity oneSubCategory;

    @Component(id = "search", parameters = {"value=searchString"})
    private TextField searchField;

    @InjectPage
    private Search search;

    public Object onActivate(EventContext context) {
        if (context.getCount() == 0) return null;
        return new HttpError(404, "Page not found.");
    }

    @Cached
    public List<CategoryEntity> getCategories() {
        return getCatalogService().findAllRootCategories();
    }

    public Object onSubmitFromSearchForm() {
        if (StringUtils.isNotEmpty(searchString)) {
            search.setSearchString(searchString);
            return search;
        } else {
            return null;
        }
    }

    public boolean isCookHasCatalog() {
        return !isAdmin() && isUser() && getCatalogService().findNotSetupCatalogs(getAuthenticatedUser()).size() > 0;
    }

    public Object[] getCatalogContext() {
        return new Object[]{FormEvent.UPDATE, getCatalogService().findNotSetupCatalogs(getAuthenticatedUser()).get(0).getId()};
    }

}
