package org.ohm.gastro.service.impl;

import org.apache.commons.lang.StringUtils;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.CommentableEntity;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.CommentRepository;
import org.ohm.gastro.reps.ConversationRepository;
import org.ohm.gastro.service.ConversationService;
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.service.RatingModifier;
import org.ohm.gastro.service.RatingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ezhulkov on 24.11.14.
 */
@Component
@Transactional
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final CommentRepository commentRepository;
    private final MailService mailService;

    @Autowired
    public ConversationServiceImpl(ConversationRepository conversationRepository, final CommentRepository commentRepository, final MailService mailService) {
        this.conversationRepository = conversationRepository;
        this.commentRepository = commentRepository;
        this.mailService = mailService;
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

    @Override
    public List<CommentEntity> findAllComments(final OrderEntity order, final UserEntity author) {
        return commentRepository.findAllByEntityAndAuthor(order, author);
    }

    @Override
    public void saveComment(CommentEntity comment) {
        commentRepository.save(comment);
    }

    @Override
    public void deleteComment(final Long cId) {
        commentRepository.delete(cId);
    }

    @Override
    public CommentEntity findComment(final Long cId) {
        return commentRepository.findOne(cId);
    }

    @Override
    public List<CommentEntity> findAllComments(final CommentableEntity entity) {
        return commentRepository.findAllByEntity(entity);
    }

    @Override
    public void placeTenderReply(final OrderEntity tender, final CommentEntity reply, final UserEntity author) {
        if (StringUtils.isEmpty(reply.getText())) return;
        reply.setEntity(tender);
        reply.setAuthor(author);
        if (reply.getId() == null) reply.setDate(new Date());
        commentRepository.save(reply);
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("username", tender.getCustomer().getFullName());
                put("address", tender.getOrderUrl());
                put("text", reply.getText());
            }
        };
        mailService.sendMailMessage(tender.getCustomer(), MailService.ORDER_COMMENT, params);
    }

    @Override
    @RatingModifier
    public void placeComment(@RatingTarget final CommentableEntity entity, final CommentEntity comment, final UserEntity author) {
        if (StringUtils.isEmpty(comment.getText()) || author == null) return;
        comment.setEntity(entity);
        comment.setAuthor(author);
        if (comment.getId() == null) comment.setDate(new Date());
        commentRepository.save(comment);
        if (entity.getCommentableType() == CommentableEntity.Type.CATALOG) {
            CatalogEntity catalog = (CatalogEntity) entity;
            final Map<String, Object> params = new HashMap<String, Object>() {
                {
                    put("address", catalog.getFullUrl());
                    put("text", comment);
                    put("username", catalog.getUser().getFullName());
                    put("catalog", catalog.getId());
                }
            };
            mailService.sendMailMessage(catalog.getUser().getEmail(), MailService.CATALOG_RATE, params);
        } else if (entity.getCommentableType() == CommentableEntity.Type.USER) {
            UserEntity user = (UserEntity) entity;
            final Map<String, Object> params = new HashMap<String, Object>() {
                {
                    put("address", user.getFullUrl());
                    put("text", comment);
                    put("username", user.getFullName());
                    put("user", user);
                }
            };
            mailService.sendMailMessage(user.getEmail(), MailService.USER_RATE, params);
        }
    }

}
