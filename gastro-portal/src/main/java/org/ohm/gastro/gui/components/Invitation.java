package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.AppResults;
import org.ohm.gastro.util.CommonsUtils;

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

    public void onFailureFromApplicationAjaxForm() {
        error = true;
    }

    public Object onSubmitFromApplicationAjaxForm() {
        if (CommonsUtils.checkAjaxBotRequest(getHttpServletRequest())) return new HttpError(403, "Not bots allowed. Bender go home!");
        if (error) return applicationFormBlock;
        getUserService().processApplicationRequest(eMail, fullName, about);
        return getPageLinkSource().createPageRenderLinkWithContext(AppResults.class, eMail);
    }

}
