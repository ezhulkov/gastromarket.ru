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
    private final int totalUnread;
    private final int messagesCount;

    public ConversationDTO(List<CommentEntity> messages, ConversationEntity conversation, UserEntity user, int totalUnread, final int messagesCount) {
        this.totalUnread = totalUnread;
        this.messagesCount = messagesCount;
        this.messages = messages == null ? null : messages.stream().map(t -> new MessageDTO(t, user)).collect(Collectors.toList());
        this.conversation = conversation;
    }

    public Long getId() {
        return conversation.getId();
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public int getTotalUnread() {
        return totalUnread;
    }

    public UserDTO getAuthor() {
        return new UserDTO(conversation.getAuthor());
    }

    public UserDTO getOpponent() {
        return new UserDTO(conversation.getOpponent());
    }

    public int getMessagesCount() {
        return messagesCount;
    }

}
