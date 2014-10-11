package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Login extends BaseComponent {

    @Property
    private boolean error;

    public void onActivate(boolean error) {
        this.error = error;
    }

}