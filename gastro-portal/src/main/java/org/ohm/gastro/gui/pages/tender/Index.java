package org.ohm.gastro.gui.pages.tender;

import com.google.common.collect.Lists;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Cookies;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.AbstractBaseEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.dto.Breadcrumb;
import org.ohm.gastro.gui.pages.AbstractPage;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Index extends AbstractPage {

    @Property
    private OrderEntity order;

    @Inject
    private Cookies cookies;

    public Object onActivate() {
        return onActivate(null);
    }

    public synchronized Object onActivate(Long orderId) {
        order = getOrderService().findOrder(orderId);
        if (order == null) return new HttpError(404, "Page not found.");
        final String cookieName = "tender_seen_" + order.getId();
        boolean dirty = false;
        if (cookies.readCookieValue(cookieName) == null) {
            cookies.writeCookieValue(cookieName, "true");
            order.setViewsCount(order.getViewsCount() + 1);
            dirty = true;
        }
        if (isCook()) {
            order.addCookViewEvent(getAuthenticatedUser().getFirstCatalog().map(AbstractBaseEntity::getId).orElse(null));
            dirty = true;
        }
        if (dirty) getOrderService().saveOrder(order);
        return true;
    }

    public Object[] onPassivate() {
        return new Object[]{order.getId()};
    }

    public String getKeywords() {
        return getMessages().format("page.keywords.tender", order.getName());
    }

    @Override
    public String getTitle() {
        return order == null ? "" : order.getName();
    }

    @Override
    public java.util.List<Breadcrumb> getBreadcrumbsContext() {
        return Lists.newArrayList(mainPage,
                                  Breadcrumb.of(getMessages().get(List.class.getName()), List.class),
                                  Breadcrumb.of(getTitle(), this.getClass())
        );
    }

}
