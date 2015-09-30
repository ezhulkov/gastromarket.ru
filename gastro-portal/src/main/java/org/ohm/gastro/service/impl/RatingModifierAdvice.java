package org.ohm.gastro.service.impl;

import com.google.common.collect.Lists;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.service.CatalogService;
import org.ohm.gastro.service.RatingService;
import org.ohm.gastro.service.RatingTarget;
import org.ohm.gastro.trait.Logging;
import org.slf4j.MDC;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by ezhulkov on 02.05.15.
 */
@Component
public class RatingModifierAdvice implements AfterReturningAdvice, Logging {

    private final ApplicationContext applicationContext;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private static volatile ScheduledFuture scheduledFuture = null;

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
        final List<CatalogEntity> catalogs = targetObject instanceof CatalogEntity ?
                Lists.newArrayList((CatalogEntity) targetObject) :
                targetObject instanceof UserEntity ? catalogService.findAllCatalogs((UserEntity) targetObject) : null;

        if (catalogs != null) {
            synchronized (this) {
                if (scheduledFuture != null) scheduledFuture.cancel(false);
                final Map<String, String> mdc = MDC.getCopyOfContextMap();
                scheduledFuture = scheduledExecutorService.schedule((Runnable) () -> {
                                                                        try {
                                                                            MDC.setContextMap(mdc);
                                                                            catalogs.forEach(ratingService::updateRating);
                                                                        } catch (Exception ex) {
                                                                            logger.error("", ex);
                                                                        }
                                                                    },
                                                                    5000,
                                                                    TimeUnit.MILLISECONDS);
            }
        }

    }

}
