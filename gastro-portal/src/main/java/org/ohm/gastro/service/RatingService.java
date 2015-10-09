package org.ohm.gastro.service;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.LogEntity;
import org.ohm.gastro.domain.LogEntity.Type;
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

    void updateRating(CatalogEntity catalog);

}
