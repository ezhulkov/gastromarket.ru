package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.annotations.Property;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class UnsubscribeRes extends AbstractPage {

    @Property
    private String eMail;

    public void onActivate(String eMail) {
        this.eMail = eMail;
    }

}
