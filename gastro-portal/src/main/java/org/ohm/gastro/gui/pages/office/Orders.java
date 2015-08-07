package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Orders extends BaseComponent {

    @Inject
    private Block newOrdersBlock;
    @Inject
    private Block activeOrdersBlock;
    @Inject
    private Block closedOrdersBlock;
    @Property
    @Persist
    private Block currentBlock;

    public void beginRender() {
        if (currentBlock == null) currentBlock = newOrdersBlock;
    }

    public Block onActionFromNewOrders() {
        currentBlock = newOrdersBlock;
        return currentBlock;
    }

    public Block onActionFromActiveOrders() {
        currentBlock = activeOrdersBlock;
        return currentBlock;
    }

    public Block onActionFromClosedOrders() {
        currentBlock = closedOrdersBlock;
        return currentBlock;
    }

}
