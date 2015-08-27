package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.util.CommonsUtils;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class FooterLayout extends BaseComponent {

    @Property
    private String comment;

    @Property
    private String eMail;

    @Property
    private String fullName;

    @Inject
    @Property
    private Block feedbackFormBlock;

    @Inject
    @Property
    private Block feedbackResultBlock;

    public Object onSubmitFromFeedbackForm() {
        if (CommonsUtils.checkAjaxBotRequest(getHttpServletRequest())) return new HttpError(403, "Not bots allowed. Bender go home!");
        getUserService().processFeedbackRequest(eMail, fullName, comment);
        return feedbackResultBlock;
    }

}
