package org.ohm.gastro.service.impl;

import com.google.common.collect.Lists;
import org.javatuples.Triplet;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.ohm.gastro.domain.AltIdBaseEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.UserRepository;
import org.ohm.gastro.service.ConversationService;
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.service.MailService.MailType;
import org.ohm.gastro.service.OrderService;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Component("schedulerService")
@Transactional
public class SchedulerServiceImpl implements Logging {

    private final OrderService orderService;
    private final MailService mailService;
    private final UserRepository userRepository;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final TransactionTemplate transactionTemplate;
    private final ConversationService conversationService;
    private final SchedulerRunner runner = new SchedulerRunner();
    private final static TimeUnit TRIGGER_TIME_UNIT = TimeUnit.MILLISECONDS;
    private final static long TRIGGER_TIME_PERIOD = 300000;

    @Autowired
    public SchedulerServiceImpl(final OrderService orderService,
                                final MailService mailService,
                                final UserRepository userRepository,
                                final TransactionTemplate transactionTemplate,
                                final ConversationService conversationService) {
        this.orderService = orderService;
        this.mailService = mailService;
        this.userRepository = userRepository;
        this.transactionTemplate = transactionTemplate;
        this.conversationService = conversationService;
    }

    @PostConstruct
    public void start() {
        scheduler.scheduleAtFixedRate(runner, 0, TRIGGER_TIME_PERIOD, TRIGGER_TIME_UNIT);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }

    private <T> void triggerLauncher(final List<T> items,
                                     final Predicate<T> filter,
                                     final Function<T, List<DateTime>> timeSeries,
                                     final Function<Triplet<List<DateTime>, DateTime, T>, Boolean> isLast,
                                     final BiConsumer<T, Boolean> consumer,
                                     final String name) {
        try {
            final Interval interval = new Interval(DateTime.now().minus(TRIGGER_TIME_PERIOD), DateTime.now());
            items.stream()
                    .filter(filter)
                    .forEach(item -> {
                        List<DateTime> series = timeSeries.apply(item);
                        series.stream().filter(interval::contains).findFirst().ifPresent(period -> {
                            final Boolean last = isLast.apply(Triplet.with(series, period, item));
                            logger.info("Firing '{}' for item {}, period {}, last {}", name, item, period, isLast);
                            transactionTemplate.execute(s -> {
                                consumer.accept(item, last);
                                return null;
                            });
                        });
                    });
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    private void triggerTenderExpiredSurvey(final OrderEntity tender, final Boolean last) {
        final OrderEntity localTender = orderService.findOrder(tender.getId());
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("username", localTender.getCustomer().getFullName());
                put("tender", localTender);
            }
        };
        mailService.sendMailMessage(localTender.getCustomer(), MailType.TENDER_EXPIRED_SURVEY, params);
    }

    private void triggerUnseenMessageReminder(final Object[] mi, final Boolean last) {
        final UserEntity opponent = userRepository.findOne(mi[1].equals(mi[3]) ? ((BigInteger) mi[4]).longValue() : ((BigInteger) mi[3]).longValue());
        final UserEntity author = userRepository.findOne(((BigInteger) mi[1]).longValue());
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("username", opponent.getFullName());
                put("address", String.format("http://gastromarket.ru/office/messages/%s/%s", mi[3], mi[4]));
                put("text", mi[2].toString());
                put("c_id", mi[5]);
                put("authorName", author.getFirstCatalog().map(AltIdBaseEntity::getName).orElseGet(author::getFullName));
            }
        };
        mailService.sendMailMessage(opponent, MailService.MailType.NEW_MESSAGE, params);
    }

    private void triggerOrderReadyReminder(final OrderEntity tender, final Boolean last) {
        final OrderEntity localTender = orderService.findOrder(tender.getId());
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("username", localTender.getCatalog().getUser().getFullName());
                put("address", localTender.getOrderUrl());
                put("tender", localTender);
            }
        };
        params.put("username", localTender.getCatalog().getUser().getFullName());
        mailService.sendMailMessage(localTender.getCatalog().getUser(), MailType.ORDER_READY_REMINDER, params);
    }

    private void orderRateReminder(final OrderEntity tender, final Boolean last) {
        final OrderEntity localTender = orderService.findOrder(tender.getId());
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("address", localTender.getOrderUrl());
                put("tender", localTender);
            }
        };
        if (!tender.isClientRate()) {
            params.put("username", localTender.getCustomer().getFullName());
            params.put("cook", false);
            mailService.sendMailMessage(localTender.getCustomer(), MailType.ORDER_RATE_REMINDER, params);
        }
    }

    private void triggerTenderReminder(final OrderEntity tender, final Boolean last) {
        final OrderEntity localTender = orderService.findOrder(tender.getId());
        final List<CommentEntity> replies = conversationService.findAllComments(localTender);
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("username", localTender.getCustomer().getFullName());
                put("address", localTender.getOrderUrl());
                put("tender", localTender);
                put("replies", replies);
            }
        };
        mailService.sendMailMessage(localTender.getCustomer(), MailType.TENDER_REMINDER, params);
        if (Boolean.TRUE == last) mailService.sendSmsMessage(localTender.getCustomer(), MailService.SmsType.TENDER_REMINDER, params);
    }

    private void orderCloseReminder(final OrderEntity order, final Boolean last) {
        final OrderEntity localOrder = orderService.findOrder(order.getId());
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("address", localOrder.getOrderUrl());
                put("order", localOrder);
            }
        };
        mailService.sendMailMessage(localOrder.getCatalog().getUser(), MailType.ORDER_CLOSE_REMINDER, params);
    }

    private class SchedulerRunner implements Runnable {
        @Override
        public void run() {
            try {
                logger.debug("Check for trigger send");
                final List<OrderEntity> allOrders = orderService.findAllOrders();
                final List<Object[]> allUnseenMessages = conversationService.findUnseenMessages();
                triggerLauncher(allUnseenMessages,
                                t -> true,
                                t -> {
                                    final DateTime time = new DateTime(t[0]);
                                    return Lists.newArrayList(time.plusMinutes(10), time.plusHours(1), time.plusHours(3));
                                },
                                t -> false,
                                SchedulerServiceImpl.this::triggerUnseenMessageReminder,
                                "MESSAGE_REMINDER");
                triggerLauncher(allOrders,
                                t -> t.isTender() && t.getStatus() == Status.NEW && t.getCatalog() == null && !t.isTenderExpired() && !conversationService.findAllComments(t).isEmpty(),
                                t -> Lists.newArrayList(t.getDateAsJoda().plusHours(1), t.getDateAsJoda().plusDays(1), t.getDateAsJoda().plusDays(3)),
                                t -> {
                                    final List<DateTime> series = t.getValue0();
                                    final DateTime period = t.getValue1();
                                    final OrderEntity item = t.getValue2();
                                    final int pos = series.indexOf(period);
                                    return pos == series.size() - 1 || series.get(pos + 1).isAfter(item.getDueDateAsJoda());
                                },
                                SchedulerServiceImpl.this::triggerTenderReminder,
                                "TENDER_REMINDER");
                triggerLauncher(allOrders,
                                t -> t.getCatalog() != null && t.getMetaStatus() == Status.ACTIVE && !t.isTenderExpired(),
                                t -> Lists.newArrayList(t.getDueDateAsJoda().minusDays(1).minusHours(6)),
                                t -> false,
                                SchedulerServiceImpl.this::triggerOrderReadyReminder,
                                "ORDER_READY_REMINDER");
                triggerLauncher(allOrders,
                                t -> t.isTenderExpired() && !conversationService.findAllComments(t).isEmpty(),
                                t -> Lists.newArrayList(t.getDueDateAsJoda().plusHours(12)),
                                t -> false,
                                SchedulerServiceImpl.this::triggerTenderExpiredSurvey,
                                "TENDER_EXPIRED_SURVEY");
                triggerLauncher(allOrders,
                                t -> t.getStatus() == Status.CLOSED && (!t.isClientRate()),
                                t -> Lists.newArrayList(t.getClosedDateAsJoda().plusDays(1)),
                                t -> false,
                                SchedulerServiceImpl.this::orderRateReminder,
                                "ORDER_RATE_REMINDER");
                triggerLauncher(allOrders,
                                t -> t.getStatus() != Status.CLOSED && t.getStatus() != Status.CANCELLED && t.getCatalog() != null && t.getDueDate() != null && t.getDueDate().before(new Date()),
                                t -> Lists.newArrayList(t.getDueDateAsJoda().plusDays(1), t.getDueDateAsJoda().plusDays(2), t.getDueDateAsJoda().plusDays(3)),
                                t -> false,
                                SchedulerServiceImpl.this::orderCloseReminder,
                                "ORDER_CLOSE_REMINDER");

            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

}
