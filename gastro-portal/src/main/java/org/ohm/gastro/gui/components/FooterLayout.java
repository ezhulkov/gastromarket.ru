package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.gui.mixins.BaseComponent;

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

    public Object onSubmitFromFeedbackAjaxForm() {
        getUserService().processFeedbackRequest(eMail, fullName, comment);
        return feedbackResultBlock;
    }

}
