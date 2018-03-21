package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.gui.pages.AbstractPage;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Payment extends AbstractPage {

    @Property
    private boolean error;

    public void onActivate(boolean error) {
        this.error = true;
    }

}
