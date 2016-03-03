package org.ohm.gastro.service.impl;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.ohm.gastro.domain.AltIdBaseEntity;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.CommentableEntity;
import org.ohm.gastro.domain.CommentableEntity.Type;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.CatalogRepository;
import org.ohm.gastro.reps.CommentRepository;
import org.ohm.gastro.reps.ConversationRepository;
import org.ohm.gastro.reps.OrderRepository;
import org.ohm.gastro.reps.PhotoRepository;
import org.ohm.gastro.reps.UserRepository;
import org.ohm.gastro.service.ConversationService;
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.service.MailService.MailType;
import org.ohm.gastro.service.RatingModifier;
import org.ohm.gastro.service.RatingTarget;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by ezhulkov on 24.11.14.
 */
@Component
@Transactional
public class ConversationServiceImpl implements ConversationService, Logging {

    private final PhotoRepository photoRepository;
    private final ConversationRepository conversationRepository;
    private final CommentRepository commentRepository;
    private final CatalogRepository catalogRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final MailService mailService;

    @Autowired
    public ConversationServiceImpl(final PhotoRepository photoRepository,
                                   final ConversationRepository conversationRepository,
                                   final CommentRepository commentRepository,
                                   final CatalogRepository catalogRepository,
                                   final UserRepository userRepository,
                                   final OrderRepository orderRepository,
                                   final MailService mailService) {
        this.photoRepository = photoRepository;
        this.conversationRepository = conversationRepository;
        this.commentRepository = commentRepository;
        this.catalogRepository = catalogRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.mailService = mailService;
    }

    @Override
    public CommentableEntity getEntity(final Type entityType, final Long entityId) {
        if (entityType == Type.CATALOG) return catalogRepository.findOne(entityId);
        if (entityType == Type.USER) return userRepository.findOne(entityId);
        if (entityType == Type.ORDER) return orderRepository.findOne(entityId);
        if (entityType == Type.CONVERSATION) return conversationRepository.findOne(entityId);
        throw new RuntimeException(entityType.name());
    }

    @Override
    public List<ConversationEntity> findAllConversations(final UserEntity user) {
        return conversationRepository.findAllConversations(user);
    }

    @Override
    public List<ConversationEntity> findAllConversations() {
        return conversationRepository.findAll(new Sort(Direction.DESC, "lastActionDate"));
    }

    @Override
    public ConversationEntity find(final Long id) {
        return conversationRepository.findOne(id);
    }

    @Override
    public void save(final ConversationEntity conversation) {
        conversationRepository.save(conversation);
    }

    @Override
    public List<CommentEntity> findAllComments(final CommentableEntity entity, final UserEntity author) {
        return commentRepository.findAllByEntityAndAuthor(entity, author);
    }

    @Override
    public List<CommentEntity> findAllComments(ConversationEntity conversation, int from, int to) {
        final int count = to - from;
        if (count == 0) return Lists.newArrayList();
        final int page = from / count;
        return commentRepository.findAllByEntity(conversation, new PageRequest(page, count));
    }

    @Override
    public void deleteComment(final Long cId) {
        commentRepository.delete(cId);
    }

    @Override
    public ConversationEntity findConversation(final UserEntity author, final UserEntity opponent) {
        return conversationRepository.findConversation(author, opponent);
    }

    @Override
    public ConversationEntity findOrCreateConversation(final UserEntity author, final UserEntity opponent) {
        ConversationEntity conversation = findConversation(author, opponent);
        if (conversation == null) {
            conversation = new ConversationEntity();
            conversation.setDate(new Date());
            conversation.setAuthor(author);
            conversation.setOpponent(opponent);
            conversationRepository.save(conversation);
        }
        return conversation;
    }

    @Override
    public int getUnreadMessagesCount(final UserEntity user) {
        return conversationRepository.findAllConversations(user).stream()
                .mapToInt(t -> (int) commentRepository.findAllByEntityOrderByDateAsc(t).stream()
                        .filter(m -> t.getLastSeenDate() == null || !m.getAuthor().equals(user) && m.getDate().after(t.getLastSeenDate()))
                        .count())
                .sum();
    }

    @Override
    public Optional<CommentEntity> findLastComment(final ConversationEntity conversation) {
        return commentRepository.findAllByEntityOrderByDateDesc(conversation).stream().findFirst();
    }

    @Override
    public PhotoEntity addPhotoComment(final ConversationEntity conversation, final UserEntity user) {
        final CommentEntity comment = new CommentEntity();
        comment.setEntity(conversation);
        comment.setAuthor(user);
        comment.setText("Фотография");
        commentRepository.save(comment);
        final PhotoEntity photo = new PhotoEntity();
        photo.setComment(comment);
        photo.setType(PhotoEntity.Type.COMMENT);
        return photoRepository.save(photo);
    }

    @Override
    public int findAllCommentsCount(CommentableEntity entity) {
        return commentRepository.findAllCountByEntity(entity);
    }

    @Override
    public void saveComment(final CommentEntity comment) {
        commentRepository.save(comment);
    }

    @Override
    public CommentEntity rateCook(final OrderEntity order, final int totalPrice, final String text, final Boolean like, final String gmComment, final Boolean gmRecommend, final UserEntity caller) {
        if (totalPrice != order.getTotalPrice()) {
            logger.info("Price changed by client. Was {}, become {}", order.getTotalPrice(), totalPrice);
            order.setTotalPrice(totalPrice);
        }
        order.setClientRate(true);
        orderRepository.save(order);
        final CommentEntity comment = new CommentEntity();
        comment.setText(text);
        comment.setRating(like == Boolean.TRUE ? 1 : -1);
        placeComment(order.getCatalog(), comment, caller);
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("text", comment.getText());
                put("order", order);
                put("catalog", order.getCatalog());
                put("address", order.getCatalog().getFullUrl());
                put("gmcomment", gmComment);
                put("gmrecommend", gmRecommend);
            }
        };
        mailService.sendAdminMessage(MailType.CATALOG_RATE_ADMIN, params);
        return comment;
    }

    @Override
    public CommentEntity rateClient(final OrderEntity order, final int totalPrice, final String text, final Boolean like, final UserEntity caller) {
        order.setCookRate(true);
        orderRepository.save(order);
        final CommentEntity comment = new CommentEntity();
        comment.setText(text);
        comment.setRating(like == Boolean.TRUE ? 1 : -1);
        placeComment(order.getCustomer(), comment, caller);
        return comment;
    }

    @Override
    public CommentEntity findComment(final Long cId) {
        return commentRepository.findOne(cId);
    }

    @Override
    public List<CommentEntity> findAllComments(final CommentableEntity entity) {
        return commentRepository.findAllByEntityOrderByDateAsc(entity);
    }

    @Override
    public void placeTenderReply(final OrderEntity tender, final CommentEntity reply, final UserEntity author) {
        if (StringUtils.isEmpty(reply.getText())) return;
        reply.setEntity(tender);
        reply.setDate(new Date());
        reply.setAuthor(author);
        commentRepository.save(reply);
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("username", tender.getCustomer().getFullName());
                put("address", tender.getOrderUrl());
                put("text", reply.getText());
                put("time", reply.getReplyExpirationHours());
                put("price", reply.getBudget());
                put("photos", reply.getPhotos());
                put("order", tender);
                put("catalog", author.getFirstCatalog().orElse(null));
            }
        };
        mailService.sendMailMessage(tender.getCustomer(), MailService.MailType.ORDER_COMMENT, params);
        mailService.sendAdminMessage(MailService.MailType.ORDER_COMMENT_ADMIN, params);
    }

    @Override
    @RatingModifier
    public void placeComment(@RatingTarget final CommentableEntity entity, final CommentEntity comment, final UserEntity author) {
        if (StringUtils.isEmpty(comment.getText()) || author == null) return;
        final Long origId = comment.getId();
        if (comment.getId() == null) {
            comment.setAuthor(author);
            comment.setDate(new Date());
            comment.setEntity(entity);
        }
        if (comment.getText() != null) comment.setText(StringEscapeUtils.escapeHtml(comment.getText()));
        commentRepository.save(comment);
        if (entity.getCommentableType() == CommentableEntity.Type.CATALOG && origId == null) {
            CatalogEntity catalog = (CatalogEntity) entity;
            final Map<String, Object> params = new HashMap<String, Object>() {
                {
                    put("address", catalog.getFullUrl());
                    put("text", comment.getText());
                    put("username", catalog.getUser().getFullName());
                    put("catalog", catalog);
                }
            };
            mailService.sendMailMessage(catalog.getUser(), MailService.MailType.CATALOG_RATE, params);
        } else if (entity.getCommentableType() == CommentableEntity.Type.USER && origId == null) {
            UserEntity user = (UserEntity) entity;
            final Map<String, Object> params = new HashMap<String, Object>() {
                {
                    put("address", user.getFullUrl());
                    put("text", comment.getText());
                    put("username", user.getFullName());
                    put("user", user);
                }
            };
            mailService.sendMailMessage(user, MailService.MailType.USER_RATE, params);
        } else if (entity.getCommentableType() == Type.CONVERSATION) {
            final ConversationEntity conversation = (ConversationEntity) entity;
            final UserEntity opponent = conversation.getOpponent(author).get();
            conversation.setLastActionDate(new Date());
            conversationRepository.save(conversation);
            mailService.scheduleSend(opponent, MailService.MailType.NEW_MESSAGE, t -> {
                final Map<String, Object> params = new HashMap<String, Object>() {
                    {
                        put("username", opponent.getFullName());
                        put("address", conversation.getFullUrl());
                        put("text", comment.getText());
                        put("conversation", conversation);
                        put("authorName", author.getFirstCatalog().map(AltIdBaseEntity::getName).orElseGet(author::getFullName));
                    }
                };
                mailService.sendMailMessage(opponent, MailService.MailType.NEW_MESSAGE, params);
            }, 5, TimeUnit.MINUTES);

        }
    }

}
