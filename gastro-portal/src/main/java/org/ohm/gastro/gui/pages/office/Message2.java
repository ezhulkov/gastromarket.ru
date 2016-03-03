package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.pages.AbstractPage;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Message2 extends AbstractPage {

    private final static int PAGE_SIZE = 40;

    @Property
    private UserEntity opponent;

    @Property
    private ConversationEntity conversation;

    public void onActivate(Long id) {
        opponent = getUserService().findUser(id);
        conversation = getConversationService().findConversation(getAuthenticatedUser(), opponent);
    }

    public Long onPassivate() {
        return opponent == null ? null : opponent.getId();
    }

}
