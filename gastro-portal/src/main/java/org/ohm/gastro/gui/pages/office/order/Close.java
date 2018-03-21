package org.ohm.gastro.gui.pages.office.order;

import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.util.CommonsUtils;

import java.util.Optional;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Close extends AbstractClosePage {

    public void onPrepare() {
        totalPrice = order.getTotalPrice().toString();
        deliveryPrice = order.getDeliveryPrice() == null ? "" : order.getDeliveryPrice().toString();
        injectPhotos.purgeSubmittedPhotos();
    }

    public Object onSubmit() {
        if (rateForm.getHasErrors()) {
            error = true;
            return null;
        }
        final Optional<Integer> totalPriceOpt = CommonsUtils.parseStrToInt(totalPrice);
        final Optional<Integer> deliveryPriceOpt = CommonsUtils.parseStrToInt(deliveryPrice);
        return totalPriceOpt.map(totalPrice -> {
            getOrderService().closeOrder(order, totalPrice, deliveryPriceOpt.orElseGet(() -> 0), gmComment, getAuthenticatedUser());
            final CommentEntity comment = getConversationService().rateClient(order, totalPrice, text, opinion, getAuthenticatedUser());
            getPhotoService().attachPhotos(comment, injectPhotos.getSubmittedPhotos());
            return CloseResults.class;
        }).orElseGet(() -> {
            error = true;
            return null;
        });
    }

}
