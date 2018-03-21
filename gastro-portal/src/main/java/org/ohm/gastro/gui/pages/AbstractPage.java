package org.ohm.gastro.gui.pages;

import com.google.common.collect.Lists;
import org.apache.tapestry5.annotations.SessionState;
import org.ohm.gastro.gui.dto.Breadcrumb;
import org.ohm.gastro.gui.dto.BreadcrumbsList;
import org.ohm.gastro.gui.dto.TitleHolder;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;

/**
 * Created by ezhulkov on 09.02.16.
 */
public abstract class AbstractPage extends BaseComponent {

    protected final Breadcrumb mainPage = Breadcrumb.of(getMessages().get(Index.class.getName()), Index.class);

    @SessionState
    private BreadcrumbsList breadcrumbsList;

    @SessionState
    private TitleHolder titleHolder;

    public void beginRender() {
        final List<Breadcrumb> breadcrumbsContext = getBreadcrumbsContext();
        if (breadcrumbsContext != null) breadcrumbsList.addAll(breadcrumbsContext);
        titleHolder.setTitle(getTitle());
    }

    public List<Breadcrumb> getBreadcrumbsContext() {
        return Lists.newArrayList(mainPage, Breadcrumb.of(getTitle(), this.getClass()));
    }

    public String getTitle() {
        final String pageClass = getClass().getName();
        return getMessages().contains(pageClass) ? getMessages().get(pageClass) : getClass().getSimpleName();
    }

}
