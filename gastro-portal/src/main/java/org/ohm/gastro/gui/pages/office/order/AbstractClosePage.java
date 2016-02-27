package org.ohm.gastro.gui.pages.office.order;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.components.comment.InjectPhotos;
import org.ohm.gastro.gui.pages.AbstractPage;

/**
 * Created by ezhulkov on 23.02.16.
 */
public abstract class AbstractClosePage extends AbstractPage {

    @Property
    protected boolean error = false;

    @Property
    protected OrderEntity order;

    @Property
    protected String totalPrice;

    @Property
    protected String text;

    @Property
    protected String gmComment;

    @Property
    protected Boolean opinion = true;

    @Property
    protected Boolean gmRecommend;

    @InjectComponent
    protected Form rateForm;

    @InjectComponent
    protected InjectPhotos injectPhotos;

    public void onActivate(Long id) {
        order = getOrderService().findOrder(id);
    }

    public void onActivate(Long id, boolean error) {
        this.order = getOrderService().findOrder(id);
        this.error = error;
        this.totalPrice = order.getTotalPrice() == null ? "" : order.getTotalPrice().toString();
    }

    public Object[] onPassivate() {
        return new Object[]{order == null ? null : order.getId(), error};
    }

    public void onPrepare() {
        totalPrice = ObjectUtils.defaultIfNull(order.getTotalPrice(), 0).toString();
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
