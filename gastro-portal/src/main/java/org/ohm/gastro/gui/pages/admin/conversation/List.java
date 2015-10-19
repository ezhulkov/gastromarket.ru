package org.ohm.gastro.gui.pages.admin.conversation;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class List extends BaseComponent {

    @Property
    private ConversationEntity oneConversation;

    @Cached
    public java.util.List<ConversationEntity> getConversations() {
        return getConversationService().findAllConversations();
    }

}
