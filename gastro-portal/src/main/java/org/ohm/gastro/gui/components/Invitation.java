package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.Region;
import org.ohm.gastro.filter.RegionFilter;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.AppResults;
import org.ohm.gastro.util.CommonsUtils;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Invitation extends BaseComponent {

    @Parameter
    private boolean forceShow = true;

    @Property
    private String about;

    @Property
    private Region region;

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

    public void beginRender() {
        region = RegionFilter.getCurrentRegion();
    }

    public void onFailureFromApplicationAjaxForm() {
        error = true;
    }

    public Object onSubmitFromApplicationAjaxForm() {
        if (CommonsUtils.checkAjaxBotRequest(getHttpServletRequest())) return null;
        if (error) return applicationFormBlock;
        getUserService().processApplicationRequest(eMail, region, fullName, about, sourceInfo);
        return getPageLinkSource().createPageRenderLinkWithContext(AppResults.class, eMail);
    }

    public boolean isShow() {
        return forceShow;
    }

}
