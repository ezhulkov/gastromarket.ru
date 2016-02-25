package org.ohm.gastro.gui.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 14.02.15.
 */
public class Modal extends BaseComponent {

    @Property
    @Parameter(name = "caption", allowNull = false, required = true, defaultPrefix = BindingConstants.LITERAL)
    private String caption;

    @Property
    @Parameter(name = "modalId", allowNull = false, required = true, defaultPrefix = BindingConstants.LITERAL)
    private String modalId;

    @Property
    @Parameter(name = "additionalClass", allowNull = false, required = false, defaultPrefix = BindingConstants.LITERAL)
    private String additionalClass;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private boolean lock;

    public String getKeyboard() {
        return Boolean.toString(!lock);
    }

    public String getBackdrop() {
        return lock ? "static" : "";
    }

}
