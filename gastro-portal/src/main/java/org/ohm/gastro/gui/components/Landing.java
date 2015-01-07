package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Landing extends BaseComponent {

    @Property
    private String about;

    @Property
    private String eMail;

    @Property
    private String fullName;

    @Inject
    private Block applicationResultBlock;

    @Property
    private boolean error = false;

    public void onFailureFromApplicationForm() {
        error = true;
    }

    public Block onSubmitFromApplicationForm() {
        if (!error) {
            return applicationResultBlock;
        }
        return null;
    }

}
