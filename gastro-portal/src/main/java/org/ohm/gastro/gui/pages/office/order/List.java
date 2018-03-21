package org.ohm.gastro.gui.pages.office.order;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.pages.AbstractPage;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class List extends AbstractPage {

    @Property
    @Inject
    private Block ordersBlock;

    public void onActivate(boolean b, OrderEntity.Status status) {
        onActivate(status);
    }

    public void onActivate(OrderEntity.Status status) {

    }

}
