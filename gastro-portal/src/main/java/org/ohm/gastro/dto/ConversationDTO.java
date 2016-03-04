package org.ohm.gastro.dto;

import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.domain.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 03.03.16.
 */
public class ConversationDTO {

    private final List<MessageDTO> messages;
    private final ConversationEntity conversation;
    private final UserEntity user;
    private final int totalUnread;

    public ConversationDTO(List<CommentEntity> messages, ConversationEntity conversation, UserEntity user, int totalUnread) {
        this.totalUnread = totalUnread;
        this.messages = messages == null ? null : messages.stream().map(t -> new MessageDTO(t, user)).collect(Collectors.toList());
        this.conversation = conversation;
        this.user = user;
    }

    public Long getId() {
        return conversation.getId();
    }

    public UserDTO getOpponent() {
        return new UserDTO(conversation.getOpponent(user).get());
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public int getTotalUnread() {
        return totalUnread;
    }

}
