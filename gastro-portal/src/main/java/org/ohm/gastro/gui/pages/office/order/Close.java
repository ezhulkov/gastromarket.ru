package org.ohm.gastro.gui.pages.office.order;

import org.apache.commons.lang.StringUtils;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Close extends AbstractClosePage {

    public Object onSubmit() {
        if (StringUtils.isEmpty(getTotalPrice()) || getRateForm().getHasErrors()) {
            setError(true);
            return null;
        }
        if (StringUtils.isNotEmpty(getTotalPrice())) {
            final int tp = Integer.parseInt(getTotalPrice());
            if (tp > 0) {
                getOrderService().closeOrder(getOrder(), tp, getGmComment(), getAuthenticatedUser());
                return CloseResults.class;
            }
        }
        return null;
    }

}
