package org.ohm.gastro.service.impl;

import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.CatalogService;
import org.ohm.gastro.service.RatingService;
import org.ohm.gastro.service.UserService;
import org.ohm.gastro.trait.Logging;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.Optional;
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
        final UserService userService = applicationContext.getBean(UserService.class);

        final Optional<UserEntity> userOpt = BaseComponent.getAuthenticatedUser(userService);
        userOpt.ifPresent(user -> {
            synchronized (this) {
                if (scheduledFuture != null) scheduledFuture.cancel(false);
                scheduledFuture = scheduledExecutorService.schedule((Runnable) () -> {
                                                                        try {
                                                                            catalogService.findAllCatalogs(user).forEach(ratingService::updateRating);
                                                                        } catch (Exception ex) {
                                                                            logger.error("", ex);
                                                                        }
                                                                    },
                                                                    5000,
                                                                    TimeUnit.MILLISECONDS);
            }
        });

    }

}
