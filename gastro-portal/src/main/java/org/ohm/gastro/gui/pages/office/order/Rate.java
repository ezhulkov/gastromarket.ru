package org.ohm.gastro.gui.pages.office.order;

import org.apache.commons.lang.StringUtils;
import org.ohm.gastro.domain.CommentEntity;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Rate extends AbstractClosePage {

    public Object onSubmit() {
        if (StringUtils.isEmpty(totalPrice) || rateForm.getHasErrors()) {
            error = true;
            return null;
        }
        final int tp = Integer.parseInt(totalPrice);
        if (tp > 0) {
            final CommentEntity comment = getConversationService().rateCook(order, tp, text, opinion, gmComment, gmRecommend, getAuthenticatedUser());
            getPhotoService().attachPhotos(comment, injectPhotos.getSubmittedPhotos());
            return RateResults.class;
        } else {
            error = true;
            return null;
        }
    }

}
