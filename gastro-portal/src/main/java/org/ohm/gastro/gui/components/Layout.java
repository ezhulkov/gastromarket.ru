package org.ohm.gastro.gui.components;

import org.apache.commons.lang.ObjectUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Layout extends BaseComponent {

    @Parameter(name = "title", required = false)
    private String title;

    @Parameter(name = "description", required = false)
    private String description;

    @Parameter(name = "keywords", required = false)
    private String keywords;

    @Parameter(name = "socialImage", required = false)
    private String socialImage;

    @Property
    @Parameter(name = "header", required = false, value = "true", defaultPrefix = BindingConstants.PROP)
    private boolean header;

    @Property
    @Parameter(name = "footer", required = false, value = "true")
    private boolean footer;

    @Property
    @Parameter(name = "bottomBlock", defaultPrefix = "literal")
    private Block bottomBlock;

    public String getDescription() {
        return (String) ObjectUtils.defaultIfNull(description, getMessages().get("page.description"));
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
