package org.ohm.gastro.gui.pages.office;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.pages.AbstractPage;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class CookRate extends AbstractPage {

    @Property
    private boolean error = false;

    @Property
    private OrderEntity order;

    @Property
    private String totalPrice;

    @Property
    private String rate;

    @Property
    private String gmComment;

    @Property
    private Boolean opinion;

    @Property
    private Boolean gmRecommend;

    @InjectComponent
    private Form rateForm;

    public void onActivate(Long id) {
        order = getOrderService().findOrder(id);
    }

    public void onActivate(Long id, boolean error) {
        this.order = getOrderService().findOrder(id);
        this.error = error;
    }

    public Object[] onPassivate() {
        return new Object[]{order == null ? null : order.getId(), error};
    }

    public void onPrepare() {
        totalPrice = ObjectUtils.defaultIfNull(order.getTotalPrice(), 0).toString();
    }

    public Object onSubmit() {
        if (StringUtils.isEmpty(totalPrice) || rateForm.getHasErrors()) {
            error = true;
            return null;
        }
        final int tp = Integer.parseInt(totalPrice);
        if (tp > 0) {
            getConversationService().rateCook(order, tp, rate, opinion, gmComment, gmRecommend, getAuthenticatedUser());
        } else {
            error = true;
            return null;
        }
        return CookRateResults.class;
    }

    public boolean getLike() {
        return true;
    }

    public boolean getDislike() {
        return false;
    }

    public boolean getLike2() {
        return true;
    }

    public boolean getDislike2() {
        return false;
    }

}
