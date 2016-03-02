package org.ohm.gastro.gui.pages.office;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.gui.pages.AbstractPage;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Message2 extends AbstractPage {

    private final static int PAGE_SIZE = 40;

    @Property
    private ConversationEntity conversation;

    public void onActivate(Long id) {
        conversation = getConversationService().find(id);
    }

    public Long onPassivate() {
        return conversation == null ? null : conversation.getId();
    }

    public String getOpponentName() {
        return conversation.getOpponentName(getAuthenticatedUser());
    }

}
