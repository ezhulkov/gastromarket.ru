package org.ohm.gastro.gui.components.order;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class Info extends AbstractOrder {

    public long getClientPosRating() {
        return getConversationService().findAllComments(order.getCustomer()).stream().filter(t -> t.getRating() > 0).count();
    }

    public long getClientNegRating() {
        return getConversationService().findAllComments(order.getCustomer()).stream().filter(t -> t.getRating() < 0).count();
    }

    public String getPrepaymentText() {
        if (catalog.getPrepayment() != null) {
            final float total = getTotal();
            final float prepayment = catalog.getPrepayment();
            return getMessages().format("prepayment.text", catalog.getPrepayment(), (int) Math.ceil(prepayment * total / 100));
        }
        return null;
    }

}
