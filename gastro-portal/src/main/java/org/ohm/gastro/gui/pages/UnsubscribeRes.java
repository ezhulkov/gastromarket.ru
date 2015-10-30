package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class UnsubscribeRes extends BaseComponent {

    @Property
    private String eMail;

    public void onActivate(String eMail) {
        this.eMail = eMail;
    }

}
