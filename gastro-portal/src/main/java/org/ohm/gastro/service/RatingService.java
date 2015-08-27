package org.ohm.gastro.service;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.LogEntity;
import org.ohm.gastro.domain.LogEntity.Type;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.domain.UserEntity;

import java.util.Date;
import java.util.List;

/**
 * Created by ezhulkov on 13.03.15.
 */
public interface RatingService extends ImageUploaderService<CommentEntity> {

    void registerEvent(LogEntity.Type type, UserEntity user);

    void registerEvent(Type type, CatalogEntity catalog, long orderTotalPrice);

    List<LogEntity> findEvents(UserEntity user, CatalogEntity catalog, Date dateFrom);

    List<LogEntity> findEvents(UserEntity user, Date dateFrom, Type type);

    void rateCatalog(CatalogEntity catalog, String comment, int rating, UserEntity author);

    void rateClient(UserEntity user, String comment, int rating, UserEntity author);

    void updateRating(CatalogEntity catalog);

    List<CommentEntity> findAllComments(CatalogEntity catalog);

    List<CommentEntity> findAllComments(UserEntity customer);

    List<CommentEntity> findAllComments(OrderEntity order);

    List<CommentEntity> findAllComments(CommentEntity comment);

    CommentEntity findComment(Long cId);

    void placeReply(OrderEntity order, UserEntity author, String replyText);

    void placeReply(CommentEntity comment, UserEntity author, String replyText);

    List<PhotoEntity> findAllPhotos(CommentEntity comment);

}
