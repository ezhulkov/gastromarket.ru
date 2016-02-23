package org.ohm.gastro.gui.pages.office.order;

import org.apache.commons.lang.StringUtils;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Rate extends AbstractClosePage {

    public Object onSubmit() {
        if (StringUtils.isEmpty(getTotalPrice()) || getRateForm().getHasErrors()) {
            setError(true);
            return null;
        }
        final int tp = Integer.parseInt(getTotalPrice());
        if (tp > 0) {
            getConversationService().rateCook(getOrder(), tp, getComment(), getOpinion(), getGmComment(), getGmRecommend(), getAuthenticatedUser());
            return RateResults.class;
        } else {
            setError(true);
            return null;
        }
    }

}
