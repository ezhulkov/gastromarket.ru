package org.ohm.gastro.gui.pages.office.order;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.corelib.components.Form;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.pages.AbstractPage;

/**
 * Created by ezhulkov on 23.02.16.
 */
public abstract class AbstractClosePage extends AbstractPage {

    private boolean error = false;

    private OrderEntity order;

    private String totalPrice;

    private String comment;

    private String gmComment;

    private Boolean opinion;

    private Boolean gmRecommend;

    @InjectComponent
    private Form rateForm;

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

    public boolean isError() {
        return error;
    }

    public void setError(final boolean error) {
        this.error = error;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(final OrderEntity order) {
        this.order = order;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(final String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public String getGmComment() {
        return gmComment;
    }

    public void setGmComment(final String gmComment) {
        this.gmComment = gmComment;
    }

    public Boolean getOpinion() {
        return opinion;
    }

    public void setOpinion(final Boolean opinion) {
        this.opinion = opinion;
    }

    public Boolean getGmRecommend() {
        return gmRecommend;
    }

    public void setGmRecommend(final Boolean gmRecommend) {
        this.gmRecommend = gmRecommend;
    }

    public Form getRateForm() {
        return rateForm;
    }

    public void setRateForm(final Form rateForm) {
        this.rateForm = rateForm;
    }
}
