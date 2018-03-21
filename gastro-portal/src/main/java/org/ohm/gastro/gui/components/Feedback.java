package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.util.CommonsUtils;

/**
 * Created by ezhulkov on 14.02.15.
 */
public class Feedback extends BaseComponent {

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

    public Object onSubmitFromFeedbackAjaxForm() {
        if (CommonsUtils.checkAjaxBotRequest(getHttpServletRequest())) return null;
        getUserService().processFeedbackRequest(eMail, fullName, comment);
        return feedbackResultBlock;
    }

}
