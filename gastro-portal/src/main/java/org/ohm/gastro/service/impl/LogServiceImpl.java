package org.ohm.gastro.service.impl;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.LogEntity;
import org.ohm.gastro.domain.LogEntity.Type;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.LogRepository;
import org.ohm.gastro.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by ezhulkov on 29.04.15.
 */
@Component
@Transactional
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;

    @Autowired
    public LogServiceImpl(LogRepository logRepository) {
        this.logRepository = logRepository;
    }


    @Override
    public void registerEvent(Type type, UserEntity user) {
        final LogEntity log = new LogEntity();
        log.setDate(new Date());
        log.setType(type);
        log.setUser(user);
        logRepository.save(log);
    }

    @Override
    public void registerEvent(Type type, CatalogEntity catalog) {
        final LogEntity log = new LogEntity();
        log.setDate(new Date());
        log.setType(type);
        log.setUser(catalog.getUser());
        log.setCatalog(catalog);
        logRepository.save(log);
    }

    @Override
    public List<LogEntity> findEvents(UserEntity user, CatalogEntity catalog, Date dateFrom) {
        return logRepository.findAll(user, catalog, dateFrom);
    }

    @Override
    public List<LogEntity> findEvents(UserEntity user, Date dateFrom, Type type) {
        return logRepository.findAll(user, dateFrom, type);
    }

}
