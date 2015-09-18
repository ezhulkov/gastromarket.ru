package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.AppResults;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Invitation extends BaseComponent {

    @Parameter
    private boolean forceShow = false;

    @Property
    private String about;

    @Property
    private String eMail;

    @Property
    private String fullName;

    @Property
    private String sourceInfo;

    @Inject
    @Property
    private Block applicationFormBlock;

    @Property
    private boolean error = false;

    public void onFailureFromApplicationAjaxForm() {
        error = true;
    }

    public Object onSubmitFromApplicationAjaxForm() {
        if (error) return applicationFormBlock;
        getUserService().processApplicationRequest(eMail, fullName, about, sourceInfo);
        return getPageLinkSource().createPageRenderLinkWithContext(AppResults.class, eMail);
    }

    public boolean isShow() {
        return forceShow || !isAuthenticated();
    }

}
