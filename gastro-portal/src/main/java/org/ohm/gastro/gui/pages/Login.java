package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.annotations.Property;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Login extends AbstractPage {

    @Property
    private boolean error;

    public Class onActivate() {
        if (isAuthenticated()) return Index.class;
        return null;
    }

    public Class onActivate(boolean error) {
        if (isAuthenticated()) return Index.class;
        this.error = error;
        return null;
    }

}