package org.ohm.gastro.gui.components.order;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class Info extends AbstractOrder {

    public long getClientPosRating() {
        return getRatingService().findAllComments(order.getCustomer()).stream().filter(t -> t.getRating() > 0).count();
    }

    public long getClientNegRating() {
        return getRatingService().findAllComments(order.getCustomer()).stream().filter(t -> t.getRating() < 0).count();
    }

}
