package org.ohm.gastro.dto;

import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.domain.UserEntity;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 03.03.16.
 */
public class ConversationDTO {

    private final List<MessageDTO> messages;
    private final ConversationEntity conversation;
    private final UserEntity author;

    public ConversationDTO(List<CommentEntity> messages, ConversationEntity conversation, UserEntity author) {
        this.messages = messages == null ? null : messages.stream().map(t -> new MessageDTO(t, author)).collect(Collectors.toList());
        this.conversation = conversation;
        this.author = author;
    }

    public Long getId() {
        return conversation.getId();
    }

    public UserDTO getOpponent() {
        return new UserDTO(conversation.getOpponent(author).get());
    }

    public Date getLastSeenDate() {
        return conversation.getLastSeenDate();
    }

    public Date getDate() {
        return conversation.getDate();
    }

    public Date getLastActionDate() {
        return conversation.getLastActionDate();
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

}
