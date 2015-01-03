package org.ohm.gastro.gui.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class HtmlLayout extends BaseComponent {

    @Property
    @Parameter(name = "floatingHeader", required = false, value = "true", defaultPrefix = BindingConstants.LITERAL)
    private boolean floatingHeader;

}
