package org.ohm.gastro.service;

import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.domain.UserEntity;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface ConversationService {

    List<ConversationEntity> findAllConversations(UserEntity userEntity);

    ConversationEntity find(Long id);

    void save(ConversationEntity conversation);

}
