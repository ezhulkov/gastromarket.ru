package org.ohm.gastro.service.impl;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.LogEntity.Type;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.domain.Region;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.filter.RegionFilter;
import org.ohm.gastro.reps.CatalogRepository;
import org.ohm.gastro.reps.OrderProductRepository;
import org.ohm.gastro.reps.OrderRepository;
import org.ohm.gastro.reps.UserRepository;
import org.ohm.gastro.service.ConversationService;
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.service.MailService.MailType;
import org.ohm.gastro.service.OrderService;
import org.ohm.gastro.service.RatingModifier;
import org.ohm.gastro.service.RatingService;
import org.ohm.gastro.service.RatingTarget;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ezhulkov on 12.10.14.
 */
@Component("orderService")
@Transactional
public class OrderServiceImpl implements Runnable, OrderService, Logging {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final CatalogRepository catalogRepository;
    private final MailService mailService;
    private final UserRepository userRepository;
    private final RatingService ratingService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final TransactionTemplate transactionTemplate;
    private final ConversationService conversationService;
    private final static TimeUnit TRIGGER_TIME_UNIT = TimeUnit.MILLISECONDS;
    private final static long TRIGGER_TIME_PERIOD = 30000;

    @Autowired
    public OrderServiceImpl(final OrderRepository orderRepository,
                            final OrderProductRepository orderProductRepository,
                            final CatalogRepository catalogRepository,
                            final MailService mailService,
                            final UserRepository userRepository,
                            final RatingService ratingService,
                            final TransactionTemplate transactionTemplate,
                            final ConversationService conversationService) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.catalogRepository = catalogRepository;
        this.mailService = mailService;
        this.userRepository = userRepository;
        this.ratingService = ratingService;
        this.transactionTemplate = transactionTemplate;
        this.conversationService = conversationService;
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
        executorService.shutdown();
    }

    @PostConstruct
    public void start() {
        scheduler.scheduleAtFixedRate(this, 1, TRIGGER_TIME_PERIOD, TRIGGER_TIME_UNIT);
    }

    @Override
    public OrderEntity placeOrder(final OrderEntity preOrder) {
        if (!preOrder.getProducts().isEmpty()) {
            final OrderEntity order = new OrderEntity();
            order.setDate(new Date());
            order.setCustomer(preOrder.getCustomer());
            order.setDueDateAsString(preOrder.getDueDateAsString());
            order.setPromoCode(preOrder.getPromoCode());
            order.setComment(preOrder.getComment());
            order.setProducts(preOrder.getProducts());
            order.setStatus(Status.CONFIRMED);
            order.setRegion(RegionFilter.getCurrentRegion());
            order.setWasSetup(true);
            order.setType(OrderEntity.Type.PRIVATE);
            order.setCatalog(preOrder.getCatalog());
            order.setTotalPrice(order.getProducts().stream().mapToInt(t -> t.getCount() * t.getPrice()).sum());
            orderRepository.save(order);
            order.getProducts().stream().forEach(p -> p.setOrder(order));
            orderProductRepository.save(order.getProducts());
            order.setOrderNumber(Long.toString(order.getId()));
            try {
                final Map<String, Object> params = new HashMap<String, Object>() {
                    {
                        put("products", order.getProducts());
                        put("ordernumber", order.getOrderNumber());
                        put("customer", order.getCustomer());
                        put("comment", ObjectUtils.defaultIfNull(order.getComment(), "-"));
                        put("cook", order.getCatalog());
                        put("total", order.getTotalPrice());
                        put("address", order.getOrderUrl());
                        put("region", order.getRegion());
                        put("order", order);
                    }
                };
                mailService.sendAdminMessage(MailService.MailType.NEW_ORDER_ADMIN, params);
                params.put("username", order.getCatalog().getUser().getFullName());
                mailService.sendMailMessage(order.getCatalog().getUser(), MailService.MailType.NEW_ORDER_COOK, params);
                params.put("username", order.getCustomer().getFullName());
                mailService.sendMailMessage(order.getCustomer(), MailService.MailType.NEW_ORDER_CUSTOMER, params);
            } catch (MailException e) {
                logger.error("", e);
            }
            return order;
        }
        return preOrder;
    }

    @Override
    public List<OrderEntity> findAllOrders() {
        return orderRepository.findAll(new Sort(Direction.DESC, "date"));
    }

    @Override
    public List<OrderEntity> findAllOrders(final UserEntity customer, final CatalogEntity catalog) {
        return orderRepository.findAllByCustomer(customer);
    }

    @Override
    public List<OrderEntity> findAllOrders(final CatalogEntity catalog) {
        return orderRepository.findAllByCatalog(catalog);
    }

    @Override
    public List<OrderEntity> findAllOrders(final CatalogEntity catalog, final Status status) {
        return orderRepository.findAllByCatalogAndStatus(catalog, status);
    }

    @Override
    public List<OrderEntity> findAllOrdersWithMetaStatus(final CatalogEntity catalog, final Status status) {
        return orderRepository.findAllByCatalog(catalog).stream().filter(t -> t.getMetaStatus() == status).collect(Collectors.toList());
    }

    @Override
    public void cancelOrder(OrderEntity order) {
        order.setClosedDate(new Date());
        order.setStatus(Status.CANCELLED);
        logger.info("Cancelling order {}, reason {}", order, order.getCancelReason());
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("order", order);
                put("address", order.getOrderUrl());
                put("customer", order.getCustomer());
                put("ordername", ObjectUtils.defaultIfNull(order.getName(), "№" + order.getOrderNumber()));
                put("reason", ObjectUtils.defaultIfNull(order.getCancelReason(), "-"));
            }
        };
        if (order.getCatalog() != null) ratingService.registerEvent(Type.ORDER_CANCELLED, order.getCatalog().getUser(), order.getCatalog(), null);
        mailService.sendAdminMessage(MailType.CANCEL_ORDER_ADMIN, params);
        params.put("username", order.getCustomer().getFullName());
        mailService.sendMailMessage(order.getCustomer(), MailType.CANCEL_ORDER_CUSTOMER, params);
        conversationService.findAllComments(order).forEach(t -> {
            params.put("username", t.getAuthor().getFullName());
            mailService.sendMailMessage(t.getAuthor(), MailType.CANCEL_ORDER_COOK, params);
        });
    }

    @Override
    public void closeOrder(final OrderEntity order, final int totalPrice, final String survey, final UserEntity caller) {
        logger.info("Closing order {}, survey {}", order, survey);
        if (totalPrice != order.getTotalPrice()) {
            logger.info("Price changed. Was {}, become {}", order.getTotalPrice(), totalPrice);
            order.setTotalPrice(totalPrice);
        }
        changeStatusInternal(order, Status.CLOSED, order.getCatalog(), caller, survey);
    }

    @Override
    public List<OrderEntity> findAllTenders() {
        return orderRepository.findAllByType(OrderEntity.Type.PUBLIC).stream()
                .filter(OrderEntity::isWasSetup)
                .filter(t -> t.getRegion() == RegionFilter.getCurrentRegion())
                .collect(Collectors.toList());
    }

    @Override
    public OrderEntity placeTender(final OrderEntity tender, final UserEntity caller) {
        tender.setDate(new Date());
        tender.setType(OrderEntity.Type.PUBLIC);
        tender.setStatus(Status.NEW);
        tender.setWasSetup(false);
        tender.setRegion(RegionFilter.getCurrentRegion());
        tender.setCustomer(caller);
        orderRepository.save(tender);
        tender.setOrderNumber(Long.toString(tender.getId()));
        orderRepository.save(tender);
        logger.info("Placing tender {}", tender);
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("username", tender.getCustomer().getFullName());
                put("customer", tender.getCustomer());
                put("comment", ObjectUtils.defaultIfNull(tender.getComment(), "-"));
                put("total", ObjectUtils.defaultIfNull(tender.getTotalPrice(), "-"));
                put("date", ObjectUtils.defaultIfNull(tender.getDueDateAsString(), "-"));
                put("region", tender.getRegion());
                put("address", tender.getOrderUrl());
                put("tender", tender);
                put("recipients", getRecipients(ObjectUtils.defaultIfNull(tender.getRegion(), Region.DEFAULT)));
            }
        };
        mailService.sendAdminMessage(MailService.MailType.NEW_TENDER_ADMIN, params);
        return tender;
    }

    @Override
    public OrderEntity attachTender(CatalogEntity catalog, OrderEntity order, UserEntity caller) {
        if (order.getCatalog() != null) return order;
        order.setCatalog(catalog);
        order.setStatus(Status.CONFIRMED);
        order.setAttachTime(new Date());
        saveOrder(order, caller);
        try {
            final Map<String, Object> params = new HashMap<String, Object>() {
                {
                    put("address", order.getOrderUrl());
                    put("tender", order);
                    put("catalog", catalog);
                    put("customer", order.getCustomer());
                }
            };
            params.put("username", catalog.getUser().getFullName());
            mailService.sendMailMessage(catalog.getUser(), MailService.MailType.TENDER_ATTACHED_COOK, params);
            conversationService.findAllComments(order).stream().filter(t -> !catalog.getUser().equals(t.getAuthor())).forEach(t -> {
                params.put("username", t.getAuthor().getFullName());
                mailService.sendMailMessage(t.getAuthor(), MailType.TENDER_ATTACHED_ALL_COOKS, params);
            });
            params.put("username", order.getCustomer().getFullName());
            mailService.sendMailMessage(order.getCustomer(), MailService.MailType.TENDER_ATTACHED_CUSTOMER, params);
            mailService.sendAdminMessage(MailService.MailType.TENDER_ATTACHED_ADMIN, params);
        } catch (MailException e) {
            logger.error("", e);
        }
        return order;
    }

    @Override
    public void sendTenderAnnonce(OrderEntity tender) {
        if (tender.isAnnonceSent()) return;
        final Region region = ObjectUtils.defaultIfNull(tender.getRegion(), Region.DEFAULT);
        tender.setWasSetup(true);
        tender.setAnnonceSent(true);
        orderRepository.save(tender);
        final List<UserEntity> rcpt = getRecipients(region);
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("username", tender.getCustomer().getFullName());
                put("customer", tender.getCustomer());
                put("total", tender.getTotalPrice());
                put("comment", ObjectUtils.defaultIfNull(tender.getComment(), "-"));
                put("date", ObjectUtils.defaultIfNull(tender.getDueDateAsString(), "-"));
                put("address", tender.getOrderUrl());
                put("tender", tender);
                put("cooks", rcpt);
            }
        };
        params.put("username", tender.getCustomer().getFullName());
        mailService.sendMailMessage(tender.getCustomer(), MailService.MailType.NEW_TENDER_CUSTOMER, params);
        executorService.execute(() -> {
            try {
                logger.info("Sending to {} cooks, region {}", rcpt.size(), region);
                rcpt.forEach(cook -> {
                    params.put("username", cook.getFullName());
                    params.put("email", cook.getEmail());
                    mailService.sendMailMessage(cook, MailService.MailType.NEW_TENDER_COOK, params);
                });
            } catch (MailException e) {
                logger.error("", e);
            }
        });
    }

    private List<UserEntity> getRecipients(Region region) {
        return catalogRepository.findAllActive(region).stream()
                .map(CatalogEntity::getUser)
                .distinct()
                .filter(UserEntity::isSubscribeEmail)
                .collect(Collectors.toList());
    }

    @Override
    public OrderEntity saveTender(final OrderEntity tender, final UserEntity caller) {
        if (tender == null || !tender.isAccessAllowed(caller)) return tender;
        tender.setLastModified(new Date());
        return orderRepository.save(tender);
    }

    @Override
    public OrderEntity findOrder(final Long id) {
        return id == null ? null : orderRepository.findOne(id);
    }

    @Override
    public OrderEntity saveOrder(OrderEntity order) {
        return orderRepository.save(order);
    }

    @Override
    public OrderEntity saveOrder(final OrderEntity order, final UserEntity caller) {
        if (order == null || !order.isAccessAllowed(caller)) return order;
        if (order.getType() == OrderEntity.Type.PRIVATE) order.setTotalPrice(order.getProducts().stream().mapToInt(t -> t.getCount() * t.getPrice()).sum());
        return orderRepository.save(order);
    }

    @Override
    public void deleteProduct(final Long oid, final Long pid, final UserEntity caller) {
        final OrderEntity order = orderRepository.findOne(oid);
        final OrderProductEntity product = orderProductRepository.findOne(pid);
        if (order == null || !order.isAccessAllowed(caller)) return;
        product.setCount(product.getCount() - 1);
        if (product.getCount() <= 0) {
            order.getProducts().remove(product);
            orderProductRepository.delete(product);
        } else {
            orderProductRepository.save(product);
        }
        if (order.getType() == OrderEntity.Type.PRIVATE) order.setTotalPrice(order.getProducts().stream().mapToInt(t -> t.getCount() * t.getPrice()).sum());
        orderRepository.save(order);
    }

    @Override
    @RatingModifier
    public void changeStatus(final OrderEntity order, final Status status, @RatingTarget final CatalogEntity catalog, final UserEntity caller) {
        changeStatusInternal(order, status, catalog, caller, "");
    }

    @Override
    public List<OrderProductEntity> findAllItems(final OrderEntity order) {
        return orderProductRepository.findAllByOrder(order);
    }

    @Override
    public void run() {
        try {
            logger.debug("Check for trigger send");
            final List<OrderEntity> allOrders = findAllOrders();
            triggerLauncher(allOrders,
                            t -> t.isTender() && t.getStatus() == Status.NEW && t.getCatalog() == null && !t.isTenderExpired() && !conversationService.findAllComments(t).isEmpty(),
                            t -> Stream.of(t.getDateAsJoda().plusHours(1), t.getDateAsJoda().plusDays(1), t.getDateAsJoda().plusDays(3)),
                            this::triggerTenderReminder,
                            "TENDER_REMINDER");
            triggerLauncher(allOrders,
                            t -> t.getCatalog() != null && t.getMetaStatus() == Status.ACTIVE && !t.isTenderExpired(),
                            t -> Stream.of(t.getDueDateAsJoda().minusDays(1).minusHours(6)),
                            this::triggerOrderReadyReminder,
                            "ORDER_READY_REMINDER");
            triggerLauncher(allOrders,
                            t -> t.isTenderExpired() && !conversationService.findAllComments(t).isEmpty(),
                            t -> Stream.of(t.getDueDateAsJoda().plusHours(12)),
                            this::triggerTenderExpiredSurvey,
                            "TENDER_EXPIRED_SURVEY");
            triggerLauncher(allOrders,
                            t -> t.getStatus() == Status.CLOSED && (!t.isClientRate() || !t.isCookRate()),
                            t -> Stream.of(t.getClosedDateAsJoda().plusDays(1)),
                            this::orderRateReminder,
                            "ORDER_RATE_REMINDER");
            triggerLauncher(allOrders,
                            t -> t.getStatus() != Status.CLOSED && t.getStatus() != Status.CANCELLED && t.getCatalog() != null && t.getDueDate() != null && t.getDueDate().before(new Date()),
                            t -> Stream.of(t.getDueDateAsJoda().plusDays(1), t.getDueDateAsJoda().plusDays(2), t.getDueDateAsJoda().plusDays(3)),
                            this::orderCloseReminder,
                            "ORDER_CLOSE_REMINDER");

        } catch (Exception e) {
            logger.error("", e);
        }
    }

    private void triggerLauncher(final List<OrderEntity> allOrders, final Predicate<OrderEntity> filter, final Function<OrderEntity, Stream<DateTime>> timeSeries, final Consumer<OrderEntity> consumer, final String name) {
        final Interval interval = new Interval(DateTime.now().minus(TRIGGER_TIME_PERIOD), DateTime.now());
        allOrders.stream()
                .filter(filter)
                .filter(t -> timeSeries.apply(t).anyMatch(interval::contains))
                .peek(t -> logger.info("Firing '{}' for order {}", name, t))
                .forEach(t -> transactionTemplate.execute(s -> {
                    consumer.accept(t);
                    return null;
                }));
    }

    private void triggerTenderExpiredSurvey(final OrderEntity tender) {
        final OrderEntity localTender = orderRepository.findOne(tender.getId());
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("username", localTender.getCustomer().getFullName());
                put("tender", localTender);
            }
        };
        mailService.sendMailMessage(localTender.getCustomer(), MailService.MailType.TENDER_EXPIRED_SURVEY, params);
    }

    private void triggerOrderReadyReminder(final OrderEntity tender) {
        final OrderEntity localTender = orderRepository.findOne(tender.getId());
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("username", localTender.getCatalog().getUser().getFullName());
                put("address", localTender.getOrderUrl());
                put("tender", localTender);
            }
        };
        params.put("username", localTender.getCatalog().getUser().getFullName());
        mailService.sendMailMessage(localTender.getCatalog().getUser(), MailService.MailType.ORDER_READY_REMINDER, params);
    }

    private void orderRateReminder(final OrderEntity tender) {
        final OrderEntity localTender = orderRepository.findOne(tender.getId());
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("address", localTender.getOrderUrl());
                put("tender", localTender);
            }
        };
        if (!tender.isCookRate()) {
            params.put("username", localTender.getCatalog().getUser().getFullName());
            params.put("cook", true);
            mailService.sendMailMessage(localTender.getCatalog().getUser(), MailService.MailType.ORDER_RATE_REMINDER, params);
        }
        if (!tender.isClientRate()) {
            params.put("username", localTender.getCustomer().getFullName());
            params.put("cook", false);
            mailService.sendMailMessage(localTender.getCustomer(), MailService.MailType.ORDER_RATE_REMINDER, params);
        }
    }

    private void triggerTenderReminder(final OrderEntity tender) {
        final OrderEntity localTender = orderRepository.findOne(tender.getId());
        final List<CommentEntity> replies = conversationService.findAllComments(localTender);
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("username", localTender.getCustomer().getFullName());
                put("address", localTender.getOrderUrl());
                put("tender", localTender);
                put("replies", replies);
            }
        };
        mailService.sendMailMessage(localTender.getCustomer(), MailService.MailType.TENDER_REMINDER, params);
    }

    private void orderCloseReminder(final OrderEntity order) {
        final OrderEntity localOrder = orderRepository.findOne(order.getId());
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("address", localOrder.getOrderUrl());
                put("order", localOrder);
            }
        };
        mailService.sendMailMessage(localOrder.getCatalog().getUser(), MailService.MailType.ORDER_CLOSE_REMINDER, params);
    }

    private void changeStatusInternal(final OrderEntity order, final Status status, @RatingTarget final CatalogEntity catalog, final UserEntity caller, final String survey) {
        if (order == null || !order.isAccessAllowed(caller)) return;
        final UserEntity customer = order.getCustomer();
        final UserEntity referrer = customer.getReferrer();
        order.setStatus(status);

        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("status", status);
                put("products", order.getProducts());
                put("ordernumber", order.getOrderNumber());
                put("customer", order.getCustomer());
                put("comment", ObjectUtils.defaultIfNull(order.getComment(), "-"));
                put("cook", order.getCatalog());
                put("total", order.getTotalPrice());
                put("address", order.getOrderUrl());
                put("region", order.getRegion());
                put("order", order);
                put("survey", survey);
            }
        };
        if (status == Status.CLOSED) {
            order.setClosedDate(new Date());
            ratingService.registerEvent(Type.ORDER_DONE, catalog.getUser(), catalog, order.getTotalPrice());
            final int bonus = order.getBonus();
            customer.giveBonus(bonus);
            logger.info("Crediting {} with {} bonuses, total {}", customer, bonus, customer.getBonus());
            userRepository.save(customer);
            ratingService.registerEvent(Type.BONUS, customer, null, bonus);
            if (referrer != null) {
                final int referralBonus = order.getReferrerBonus();
                referrer.giveBonus(referralBonus);
                logger.info("Crediting {} with {} bonuses, total {}", referrer, referralBonus, referrer.getBonus());
                userRepository.save(referrer);
                ratingService.registerEvent(Type.BONUS, referrer, null, referralBonus);
            }
            params.put("username", customer.getFullName());
            mailService.sendMailMessage(customer, MailService.MailType.CLOSE_ORDER_CUSTOMER, params);
            params.put("username", order.getCatalog().getUser().getFullName());
            mailService.sendMailMessage(order.getCatalog().getUser(), MailService.MailType.CLOSE_ORDER_COOK, params);
            mailService.sendAdminMessage(MailService.MailType.CLOSE_ORDER_ADMIN, params);
        } else {
            params.put("username", customer.getFullName());
            mailService.sendMailMessage(customer, MailService.MailType.EDIT_ORDER, params);
            params.put("username", order.getCatalog().getUser().getFullName());
            mailService.sendMailMessage(order.getCatalog().getUser(), MailService.MailType.EDIT_ORDER, params);
        }
        orderRepository.save(order);
    }

}
