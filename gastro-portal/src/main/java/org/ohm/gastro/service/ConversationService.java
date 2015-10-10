package org.ohm.gastro.service;

import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.CommentableEntity;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.domain.UserEntity;

import java.util.List;
import java.util.Optional;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface ConversationService {

    List<ConversationEntity> findAllConversations(UserEntity userEntity);

    ConversationEntity find(Long id);

    void save(ConversationEntity conversation);

    void placeComment(CommentableEntity entity, CommentEntity comment, UserEntity author);

    List<CommentEntity> findAllComments(CommentableEntity entity);

    CommentEntity findComment(Long cId);

    void placeTenderReply(OrderEntity tender, CommentEntity reply, UserEntity author);

    List<CommentEntity> findAllComments(OrderEntity order, UserEntity author);

    void saveComment(CommentEntity comment);

    void deleteComment(Long cId);

    ConversationEntity findOrCreateConversation(final UserEntity sender, UserEntity opponent);

    int getUnreadMessagesCount(UserEntity user);

    Optional<CommentEntity> findLastComment(ConversationEntity conversation);

    PhotoEntity addPhotoComment(ConversationEntity conversation, UserEntity user);

}
