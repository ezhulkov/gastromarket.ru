package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.annotations.Property;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class CateringResults extends AbstractPage {

    @Property
    private String email;

    public void onActivate(String email) {
        this.email = email;
    }

    public String onPassivate() {
        return email;
    }


}
