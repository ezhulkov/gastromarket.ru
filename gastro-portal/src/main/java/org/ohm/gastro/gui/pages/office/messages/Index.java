package org.ohm.gastro.gui.pages.office.messages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.pages.AbstractPage;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Index extends AbstractPage {

    @Property
    private UserEntity author;

    @Property
    private UserEntity opponent;

    public Object onActivate(Long aid, Long oid) {
        author = getUserService().findUser(aid);
        opponent = getUserService().findUser(oid);
        if (!isAdmin()) {
            if (!getAuthenticatedUser().equals(author) && !getAuthenticatedUser().equals(opponent)) return new HttpError(403, "Access denied.");
        }
        return null;
    }

    public Object[] onPassivate() {
        return new Object[]{author.getId(), opponent.getId()};
    }

    public UserEntity getOpponentUser() {
        return getAuthenticatedUser().equals(author) ? opponent : author;
    }

    @Override
    public String getTitle() {
        return getOpponentUser().getLinkName();
    }

}
