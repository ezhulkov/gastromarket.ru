package org.ohm.gastro.service;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.CommentableEntity;
import org.ohm.gastro.domain.LogEntity;
import org.ohm.gastro.domain.LogEntity.Type;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.UserEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

/**
 * Created by ezhulkov on 13.03.15.
 */
public interface RatingService {

    void registerEvent(@Nonnull LogEntity.Type type, @Nonnull UserEntity user, @Nullable CatalogEntity catalog, @Nullable Integer data);

    List<LogEntity> findEvents(UserEntity user, CatalogEntity catalog, Date dateFrom);

    List<LogEntity> findEvents(UserEntity user, Date dateFrom, Type type);

    void placeComment(CommentableEntity entity, CommentEntity comment, UserEntity author);

    void updateRating(CatalogEntity catalog);

    List<CommentEntity> findAllComments(CommentableEntity entity);

    CommentEntity findComment(Long cId);

    void placeTenderReply(OrderEntity tender, CommentEntity reply, UserEntity author);

    List<CommentEntity> findAllComments(OrderEntity order, UserEntity author);

    void saveComment(CommentEntity comment);

    void deleteComment(Long cId);

}
