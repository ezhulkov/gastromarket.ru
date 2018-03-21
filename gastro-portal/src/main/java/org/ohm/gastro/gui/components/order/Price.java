package org.ohm.gastro.gui.components.order;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class Price extends AbstractOrder {

    public String getBasketMinText() {
        return getMessages().format("basket.min.text", catalog.getBasketMin());
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
