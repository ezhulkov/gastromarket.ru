package org.ohm.gastro.gui.pages.user;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
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

    public void onActivate(Long uid) {
        customer = getUserService().findUser(uid);
    }

    public Long onPassivate() {
        return customer == null ? null : customer.getId();
    }

    @Cached
    public java.util.List<CommentEntity> getComments() {
        return getRatingService().findAllComments(customer);
    }

}
