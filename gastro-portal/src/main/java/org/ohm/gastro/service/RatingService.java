package org.ohm.gastro.service;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.LogEntity;
import org.ohm.gastro.domain.LogEntity.Type;
import org.ohm.gastro.domain.UserEntity;

import java.util.Date;
import java.util.List;

/**
 * Created by ezhulkov on 13.03.15.
 */
public interface RatingService {

    void registerEvent(LogEntity.Type type, UserEntity user);

    void registerEvent(Type type, CatalogEntity catalog, long orderTotalPrice);

    List<LogEntity> findEvents(UserEntity user, CatalogEntity catalog, Date dateFrom);

    List<LogEntity> findEvents(UserEntity user, Date dateFrom, Type type);

    void rateCatalog(final CatalogEntity catalog, final String comment, final int rating, final UserEntity user);

    void updateRating(final CatalogEntity catalog);

    List<CommentEntity> findAllComments(CatalogEntity user);

}