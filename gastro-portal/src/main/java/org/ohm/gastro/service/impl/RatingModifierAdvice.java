package org.ohm.gastro.service.impl;

import com.google.common.collect.Lists;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.service.CatalogService;
import org.ohm.gastro.service.RatingService;
import org.ohm.gastro.service.RatingTarget;
import org.ohm.gastro.trait.Logging;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 02.05.15.
 */
@Component
public class RatingModifierAdvice implements AfterReturningAdvice, Logging {

    private final ApplicationContext applicationContext;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private final static Map<String, ScheduledFuture> scheduledFutures = new ConcurrentHashMap<>();

    @Autowired
    public RatingModifierAdvice(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PreDestroy
    public void shutdown() {
        this.scheduledExecutorService.shutdown();
    }

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {

        final RatingService ratingService = applicationContext.getBean(RatingService.class);
        final CatalogService catalogService = applicationContext.getBean(CatalogService.class);

        final Method realMethod = method.getDeclaringClass().isInterface() ?
                target.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes()) :
                method;

        int i;
        final Parameter[] parameters = realMethod.getParameters();
        for (i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.getAnnotation(RatingTarget.class) != null) {
                break;
            }
        }

        if (i == parameters.length || i > args.length) return;
        final Object targetObject = args[i];
        if (targetObject != null) {
            final List<CatalogEntity> catalogs = targetObject instanceof CatalogEntity ?
                    Lists.newArrayList((CatalogEntity) targetObject) :
                    targetObject instanceof UserEntity ? catalogService.findAllCatalogs((UserEntity) targetObject) : Lists.newArrayList();
            final String operationKey = catalogs.stream().sorted((o1, o2) -> o1.getId().compareTo(o2.getId())).map(t -> t.getId().toString()).collect(Collectors.joining());

            synchronized (scheduledFutures) {
                final ScheduledFuture prevScheduledFuture = scheduledFutures.remove(operationKey);
                if (prevScheduledFuture != null) prevScheduledFuture.cancel(false);
                final ScheduledFuture scheduledFuture = scheduledExecutorService.schedule((Runnable) () -> {
                                                                                              synchronized (scheduledFutures) {
                                                                                                  if (scheduledFutures.remove(operationKey) == null) return;
                                                                                              }
                                                                                              try {
                                                                                                  catalogs.forEach(ratingService::updateRating);
                                                                                              } catch (Exception ex) {
                                                                                                  logger.error("", ex);
                                                                                              }
                                                                                          },
                                                                                          5000,
                                                                                          TimeUnit.MILLISECONDS);
                scheduledFutures.put(operationKey, scheduledFuture);
            }
        }

    }

}
