package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.pages.AbstractPage;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Message extends AbstractPage {

    private final static int PAGE_SIZE = 40;

    @Property
    private UserEntity opponent;

    @Property
    private UserEntity author;

    @Property
    private ConversationEntity conversation;

    public Object onActivate(Long aid, Long oid) {
        author = getUserService().findUser(aid);
        opponent = getUserService().findUser(oid);
        conversation = getConversationService().findConversation(author, opponent);
        if (!isAdmin()) {
            if (!getAuthenticatedUser().equals(author) && !getAuthenticatedUser().equals(opponent)) return new HttpError(403, "Access denied.");
        }
        return null;
    }

    public Object[] onPassivate() {
        return opponent == null || author == null ? null : new Object[]{author.getId(), opponent.getId()};
    }

}
