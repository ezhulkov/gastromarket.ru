package org.ohm.gastro.gui.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class YaGoalSubmit {

    @Property
    @Parameter(allowNull = false, required = true, defaultPrefix = BindingConstants.LITERAL)
    private String goal;

    @Property
    @Parameter(allowNull = false, required = true, defaultPrefix = BindingConstants.LITERAL)
    private String target;

}
