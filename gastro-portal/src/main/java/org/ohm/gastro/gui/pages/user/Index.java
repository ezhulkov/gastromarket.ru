package org.ohm.gastro.gui.pages.user;

import com.google.common.collect.Lists;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.pages.AbstractPage;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Index extends AbstractPage {

    @Property
    private UserEntity customer;

    @Property
    private CommentEntity oneComment;

    public Object onActivate() {
        return onActivate(null);
    }

    public Object onActivate(Long uid) {
        if (uid == null) return new HttpError(404, "Page not found.");
        customer = getUserService().findUser(uid);
        if (customer == null) return new HttpError(404, "Page not found.");
        return true;
    }

    public Long onPassivate() {
        return customer == null ? null : customer.getId();
    }

    @Override
    public String getTitle() {
        return customer == null ? "" : customer.getFullName();
    }

    public java.util.List<CommentEntity> getCommonComments() {
        return isAuthenticated() ? getOrderService().findCommonComments(getAuthenticatedUser(), customer) : Lists.newArrayList();
    }

    public Object[] getContext() {
        return new Object[]{getAuthenticatedUser().getId(), customer.getId()};
    }

}
