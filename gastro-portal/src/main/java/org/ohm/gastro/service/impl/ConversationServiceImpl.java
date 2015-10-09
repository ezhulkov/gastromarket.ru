package org.ohm.gastro.service.impl;

import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.ConversationRepository;
import org.ohm.gastro.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by ezhulkov on 24.11.14.
 */
@Component
@Transactional
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;

    @Autowired
    public ConversationServiceImpl(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    @Override
    public List<ConversationEntity> findAllConversations(final UserEntity user) {
        return conversationRepository.findAllConversations(user);
    }

    @Override
    public ConversationEntity find(final Long id) {
        return conversationRepository.findOne(id);
    }

    @Override
    public void save(final ConversationEntity conversation) {
        conversationRepository.save(conversation);
    }

}
