package org.ohm.gastro.gui.pages.user;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Index extends BaseComponent {

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

}
