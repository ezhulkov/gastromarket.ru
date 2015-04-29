package org.ohm.gastro.service;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.LogEntity;
import org.ohm.gastro.domain.LogEntity.Type;
import org.ohm.gastro.domain.UserEntity;

import java.util.Date;
import java.util.List;

/**
 * Created by ezhulkov on 13.03.15.
 */
public interface LogService {

    void registerEvent(LogEntity.Type type, UserEntity user);

    void registerEvent(LogEntity.Type type, CatalogEntity catalog);

    List<LogEntity> findEvents(UserEntity user, CatalogEntity catalog, Date dateFrom);

    List<LogEntity> findEvents(UserEntity user, Date dateFrom, Type type);

}
