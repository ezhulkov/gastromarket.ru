package org.ohm.gastro.gui.components;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Layout extends BaseComponent {

    @Parameter(name = "title", required = false, defaultPrefix = BindingConstants.LITERAL)
    private String title;

    @Parameter(name = "breadCrumbTitle", required = false, defaultPrefix = BindingConstants.LITERAL)
    private String breadCrumbTitle;

    @Parameter(name = "description", required = false)
    private String description;

    @Property
    @Parameter(name = "pageClass", required = false, defaultPrefix = BindingConstants.LITERAL, value = " ")
    private String pageClass;

    @Property
    @Parameter(name = "forceShow", required = false, defaultPrefix = BindingConstants.LITERAL, value = "true")
    private boolean forceShow;

    @Parameter(name = "keywords", required = false)
    private String keywords;

    @Parameter(name = "socialImage", required = false)
    private String socialImage;

    @Property
    @Parameter(name = "header", required = false, value = "true", defaultPrefix = BindingConstants.PROP)
    private boolean header;

    @Property
    @Parameter(name = "breadcrumbs", required = false, value = "true", defaultPrefix = BindingConstants.PROP)
    private boolean breadcrumbs;

    @Property
    @Parameter(name = "footer", required = false, value = "true")
    private boolean footer;

    @Property
    @Parameter(name = "bottomBlock", defaultPrefix = "literal")
    private Block bottomBlock;

    public String getDescription() {
        return ObjectUtils.defaultIfNull(description, getMessages().get("page.description"));
    }

    public String getTitle() {
        return ObjectUtils.defaultIfNull(title, getMessages().get("page.title"));
    }

    public String getSocialImage() {
        return "https://gastromarket.ru" + ObjectUtils.defaultIfNull(socialImage, "/img/logo.jpg");
    }

    public String getKeywords() {
        return ObjectUtils.defaultIfNull(keywords, getMessages().get("page.keywords"));
    }

}
