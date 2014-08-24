package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.annotations.Property;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Login {

    @Property
    private boolean error;

    public void onActivate(boolean error) {
        this.error = error;
    }

}