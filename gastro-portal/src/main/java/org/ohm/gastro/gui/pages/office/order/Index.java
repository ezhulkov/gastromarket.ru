package org.ohm.gastro.gui.pages.office.order;

import com.google.common.collect.Lists;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.dto.Breadcrumb;
import org.ohm.gastro.gui.pages.AbstractPage;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Index extends AbstractPage {

    @Property
    private OrderEntity order;

    @Property
    private boolean placed;

    @Inject
    @Property
    private Block orderBlock;

    public boolean onActivate(Long orderId, boolean placed) {
        this.order = getOrderService().findOrder(orderId);
        this.placed = placed;
        return true;
    }

    public boolean onActivate(Long orderId) {
        return onActivate(orderId, false);
    }

    public Long onPassivate() {
        return order.getId();
    }

    @Override
    public String getTitle() {
        return order == null ? "" : order.getOrderName();
    }

    @Override
    public java.util.List<Breadcrumb> getBreadcrumbsContext() {
        return Lists.newArrayList(mainPage,
                                  Breadcrumb.of(getMessages().get(List.class.getName()), List.class),
                                  Breadcrumb.of(getTitle(), this.getClass())
        );
    }

}
