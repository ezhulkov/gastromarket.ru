package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.ohm.gastro.gui.dto.Breadcrumb;
import org.ohm.gastro.gui.dto.BreadcrumbsList;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class BreadCrumbs extends BaseComponent {

    @Property
    @SessionState
    private BreadcrumbsList breadcrumbsList;

    @Property
    private Breadcrumb breadcrumb;

    public String getUrl() {
        return (breadcrumb.getParams() == null ?
                getPageLinkSource().createPageRenderLink(breadcrumb.getPage()) :
                getPageLinkSource().createPageRenderLinkWithContext(breadcrumb.getPage(), breadcrumb.getParams())).getBasePath();
    }

}