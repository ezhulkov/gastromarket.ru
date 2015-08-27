package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.AppResults;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Invitation extends BaseComponent {

    @Property
    private String about;

    @Property
    private String eMail;

    @Property
    private String fullName;

    @Inject
    @Property
    private Block applicationFormBlock;

    @Property
    private boolean error = false;

    public void onFailureFromApplicationForm() {
        error = true;
    }

    public Object onSubmitFromApplicationForm() {
        if (error || getRequest().getParameter("recaptcha_verified") == null) return applicationFormBlock;
        getUserService().processApplicationRequest(eMail, fullName, about);
        return getPageLinkSource().createPageRenderLinkWithContext(AppResults.class, eMail);
    }

}
