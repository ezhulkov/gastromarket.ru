package org.ohm.gastro.service.impl;

import org.ohm.gastro.reps.MessageRepository;
import org.ohm.gastro.service.MessageService;
import org.springframework.stereotype.Component;

/**
 * Created by ezhulkov on 24.11.14.
 */
@Component
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

}
