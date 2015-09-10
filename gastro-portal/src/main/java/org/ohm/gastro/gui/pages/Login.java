package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Login extends BaseComponent {

    @Property
    private boolean error;

    public Class onActivate() {
//        if (isAuthenticated()) return Index.class;
        return null;
    }

    public Class onActivate(boolean error) {
        if (isAuthenticated()) return Index.class;
        this.error = error;
        return null;
    }

}