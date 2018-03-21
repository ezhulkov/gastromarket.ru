package org.ohm.gastro.gui.components;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 14.02.15.
 */
public class LazyImage extends BaseComponent {

    @Property
    @Parameter(name = "title", defaultPrefix = BindingConstants.PROP)
    private String title;

    @Property
    @Parameter(name = "alt", defaultPrefix = BindingConstants.PROP)
    private String alt;

    @Property
    @Parameter(name = "additionalClass", defaultPrefix = BindingConstants.LITERAL)
    private String additionalClass;

    @Property
    @Parameter(name = "source", defaultPrefix = BindingConstants.PROP)
    private String source;

    @Parameter(name = "stub", defaultPrefix = BindingConstants.LITERAL)
    private String stub;

    public String getStub(){
        return ObjectUtils.defaultIfNull(stub,source);
    }

}
