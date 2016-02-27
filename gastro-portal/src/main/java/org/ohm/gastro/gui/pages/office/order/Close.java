package org.ohm.gastro.gui.pages.office.order;

import org.apache.commons.lang.StringUtils;
import org.ohm.gastro.domain.CommentEntity;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Close extends AbstractClosePage {

    public Object onSubmit() {
        if (StringUtils.isEmpty(totalPrice) || rateForm.getHasErrors()) {
            error = true;
            return null;
        }
        if (StringUtils.isNotEmpty(totalPrice)) {
            final int tp = Integer.parseInt(totalPrice);
            if (tp > 0) {
                getOrderService().closeOrder(order, tp, gmComment, getAuthenticatedUser());
                final CommentEntity comment = getConversationService().rateClient(order, tp, text, opinion, getAuthenticatedUser());
                getPhotoService().attachPhotos(comment, injectPhotos.getSubmittedPhotos());
                return CloseResults.class;
            }
        }
        return null;
    }

}
