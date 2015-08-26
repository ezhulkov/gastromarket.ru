package org.ohm.gastro.service.impl;

import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.MessageRepository;
import org.ohm.gastro.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by ezhulkov on 24.11.14.
 */
@Component
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public int getUnreadMessages(UserEntity user) {
        return messageRepository.findAllUnreadMessages(user, user.getType().name()).size();
    }
}
