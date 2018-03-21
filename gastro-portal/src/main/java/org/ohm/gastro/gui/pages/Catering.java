package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.annotations.Property;

public class Catering extends AbstractPage {

    @Property
    private String comment;

    @Property
    private String eMail;

    @Property
    private String userName;

    @Property
    private String mobile;

    public Object onSubmitFromCateringForm() {
        getOrderService().processCateringRequest(userName, mobile, eMail, comment);
        return getPageLinkSource().createPageRenderLinkWithContext(CateringResults.class, eMail);
    }


}
