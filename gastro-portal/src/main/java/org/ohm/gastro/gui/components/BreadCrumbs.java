package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.gui.dto.PageLink;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class BreadCrumbs extends BaseComponent {

    @Property
    private PageLink pageLink;

    @Property
    private int index;

    @Property
    private List<PageLink> lastPages;

    public void beginRender() {
        lastPages = getBreadCrumbs().getLastBreadCrumbs(getCurrentPage());
    }

    public boolean isLastPage() {
        return index + 1 == lastPages.size();
    }

}