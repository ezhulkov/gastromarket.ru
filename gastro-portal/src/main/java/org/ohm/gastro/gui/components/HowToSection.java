package org.ohm.gastro.gui.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 30.05.15.
 */
public class HowToSection extends BaseComponent {

    @Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String additionalClass;

}
