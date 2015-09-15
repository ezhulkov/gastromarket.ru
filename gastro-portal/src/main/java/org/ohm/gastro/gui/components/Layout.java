package org.ohm.gastro.gui.components;

import org.apache.commons.lang.ObjectUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.catalog.Wizard;
import org.ohm.gastro.gui.pages.office.Import;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Layout extends BaseComponent {

    @Parameter(name = "title", required = false)
    private String title;

    @Parameter(name = "keywords", required = false)
    private String keywords;

    @Parameter(name = "socialImage", required = false)
    private String socialImage;

    @Property
    @Parameter(name = "header", required = false, value = "true", defaultPrefix = BindingConstants.PROP)
    private boolean header;

    @Property
    @Parameter(name = "bottomBlock", defaultPrefix = "literal")
    private Block bottomBlock;

    @Cached
    public CatalogEntity getNewCatalog() {
        if (!isCook() ||
                getComponentResources().getPage() instanceof Wizard ||
                getComponentResources().getPage() instanceof Import) return null;
        return getCatalogService().findAllCatalogs(getAuthenticatedUser()).stream()
                .filter(t -> !t.isWasSetup())
                .findAny().orElse(null);
    }

    public String getDescription() {
        return getMessages().get("page.description");
    }

    public String getTitle() {
        return (String) ObjectUtils.defaultIfNull(title, getMessages().get("page.title"));
    }

    public String getSocialImage() {
        return "http://gastromarket.ru" + ObjectUtils.defaultIfNull(socialImage, "/img/logo.jpg");
    }

    public String getKeywords() {
        return (String) ObjectUtils.defaultIfNull(keywords, getMessages().get("page.keywords"));
    }

}
