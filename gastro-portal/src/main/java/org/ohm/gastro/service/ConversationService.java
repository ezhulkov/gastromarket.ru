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

    CommentableEntity getEntity(CommentableEntity.Type entityType, Long entityId);

    List<ConversationEntity> findAllConversations(UserEntity userEntity);

    List<ConversationEntity> findAllConversations();

    ConversationEntity find(Long id);

    void save(ConversationEntity conversation);

    void placeComment(CommentableEntity entity, CommentEntity comment, UserEntity author);

    List<CommentEntity> findAllComments(CommentableEntity entity);

    List<CommentEntity> findAllComments(ConversationEntity conversation, int fetchFrom, int fetchTo);

    CommentEntity findComment(Long cId);

    void placeTenderReply(OrderEntity tender, CommentEntity reply, UserEntity author);

    List<CommentEntity> findAllComments(CommentableEntity entity, UserEntity author);

    void deleteComment(Long cId);

    ConversationEntity findConversation(final UserEntity author, UserEntity opponent);

    ConversationEntity findOrCreateConversation(final UserEntity author, UserEntity opponent);

    int getUnreadMessagesCount(UserEntity user);

    Optional<CommentEntity> findLastComment(ConversationEntity conversation);

    PhotoEntity addPhotoComment(ConversationEntity conversation, UserEntity user);

    int findAllCommentsCount(CommentableEntity entity);

    void saveComment(CommentEntity comment);

    CommentEntity rateCook(OrderEntity order, final int totalPrice, String comment, Boolean like, String gmComment, Boolean gmRecommend, UserEntity caller);

    CommentEntity rateClient(OrderEntity order, final int totalPrice, String comment, Boolean like, UserEntity caller);

    void setLastSeenDate(ConversationEntity conversation, UserEntity user);

}
