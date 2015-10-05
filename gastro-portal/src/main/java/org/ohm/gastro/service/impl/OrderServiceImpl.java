package org.ohm.gastro.service.impl;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.apache.commons.lang.ObjectUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.ImageWithObject;
import org.ohm.gastro.domain.LogEntity.Type;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.CatalogRepository;
import org.ohm.gastro.reps.ImageRepository;
import org.ohm.gastro.reps.OrderProductRepository;
import org.ohm.gastro.reps.OrderRepository;
import org.ohm.gastro.reps.UserRepository;
import org.ohm.gastro.service.ImageService.FileType;
import org.ohm.gastro.service.ImageService.ImageSize;
import org.ohm.gastro.service.ImageUploader;
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.service.OrderService;
import org.ohm.gastro.service.RatingModifier;
import org.ohm.gastro.service.RatingService;
import org.ohm.gastro.service.RatingTarget;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.Collectors;

import static org.scribe.utils.Preconditions.checkNotNull;

/**
 * Created by ezhulkov on 12.10.14.
 */
@Component("orderService")
@Transactional
@ImageUploader(FileType.ORDER)
public class OrderServiceImpl implements Runnable, OrderService, Logging {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final CatalogRepository catalogRepository;
    private final ImageRepository photoRepository;
    private final MailService mailService;
    private final UserRepository userRepository;
    private final RatingService ratingService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public OrderServiceImpl(final OrderRepository orderRepository,
                            final OrderProductRepository orderProductRepository,
                            final CatalogRepository catalogRepository,
                            final ImageRepository photoRepository,
                            final MailService mailService,
                            final UserRepository userRepository,
                            final RatingService ratingService,
                            final TransactionTemplate transactionTemplate) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.catalogRepository = catalogRepository;
        this.photoRepository = photoRepository;
        this.mailService = mailService;
        this.userRepository = userRepository;
        this.ratingService = ratingService;
        this.transactionTemplate = transactionTemplate;
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
        executorService.shutdown();
    }

    @PostConstruct
    public void start() {
        scheduler.scheduleAtFixedRate(this, 1, 10, TimeUnit.MINUTES);
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
            order.setStatus(Status.ACTIVE);
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
                        put("order", order);
                    }
                };
                mailService.sendAdminMessage(MailService.NEW_ORDER_ADMIN, params);
                params.put("username", order.getCatalog().getUser().getFullName());
                mailService.sendMailMessage(order.getCatalog().getUser(), MailService.NEW_ORDER_COOK, params);
                params.put("username", order.getCustomer().getFullName());
                mailService.sendMailMessage(order.getCustomer(), MailService.NEW_ORDER_CUSTOMER, params);
            } catch (MailException e) {
                logger.error("", e);
            }
            return order;
        }
        return preOrder;
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
    public List<OrderEntity> findAllTenders() {
        return orderRepository.findAllByType(OrderEntity.Type.PUBLIC);
    }

    @Override
    public OrderEntity placeTender(final OrderEntity tender, final UserEntity caller) {
        if (tender == null || !tender.isAllowed(caller)) return tender;
        tender.setDate(new Date());
        tender.setType(OrderEntity.Type.PUBLIC);
        tender.setStatus(Status.NEW);
        tender.setWasSetup(false);
        orderRepository.save(tender);
        tender.setOrderNumber(Long.toString(tender.getId()));
        return orderRepository.save(tender);
    }

    @Override
    public OrderEntity commitTender(OrderEntity tender, UserEntity caller) {
        tender.setWasSetup(true);
        orderRepository.save(tender);
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("username", tender.getCustomer().getFullName());
                put("customer", tender.getCustomer());
                put("name", ObjectUtils.defaultIfNull(tender.getName(), ""));
                put("comment", ObjectUtils.defaultIfNull(tender.getComment(), ""));
                put("total", ObjectUtils.defaultIfNull(tender.getTotalPrice(), ""));
                put("date", ObjectUtils.defaultIfNull(tender.getDatePrintable(), "-"));
                put("address", tender.getOrderUrl());
                put("tender", tender);
                put("recipients", getRecipients());
            }
        };
        mailService.sendAdminMessage(MailService.NEW_TENDER_ADMIN, params);
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
                }
            };
            params.put("username", catalog.getUser().getFullName());
            mailService.sendMailMessage(catalog.getUser(), MailService.TENDER_ATTACHED_COOK, params);
            params.put("username", order.getCustomer().getFullName());
            mailService.sendMailMessage(order.getCustomer(), MailService.TENDER_ATTACHED_CUSTOMER, params);
        } catch (MailException e) {
            logger.error("", e);
        }
        return order;
    }

    @Override
    public void sendTenderAnnonce(OrderEntity tender) {
        if (tender.isAnnonceSent()) return;
        tender.setAnnonceSent(true);
        orderRepository.save(tender);
        final List<UserEntity> rcpt = getRecipients();
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("username", tender.getCustomer().getFullName());
                put("customer", tender.getCustomer());
                put("name", ObjectUtils.defaultIfNull(tender.getName(), ""));
                put("comment", ObjectUtils.defaultIfNull(tender.getComment(), "-"));
                put("total", ObjectUtils.defaultIfNull(tender.getTotalPrice(), "-"));
                put("date", ObjectUtils.defaultIfNull(tender.getDatePrintable(), "-"));
                put("address", tender.getOrderUrl());
                put("tender", tender);
                put("cooks", rcpt);
            }
        };
        params.put("username", tender.getCustomer().getFullName());
        mailService.sendMailMessage(tender.getCustomer(), MailService.NEW_TENDER_CUSTOMER, params);
        executorService.execute(() -> {
            try {
                logger.info("Sending to {} cooks", rcpt.size());
                rcpt.forEach(cook -> {
                    params.put("username", cook.getFullName());
                    params.put("email", cook.getEmail());
                    mailService.sendMailMessage(cook, MailService.NEW_TENDER_COOK, params);
                });
            } catch (MailException e) {
                logger.error("", e);
            }
        });
    }

    private List<UserEntity> getRecipients() {
        return catalogRepository.findAll().stream().map(CatalogEntity::getUser).distinct().filter(UserEntity::isSubscribeEmail).collect(Collectors.toList());
//        return Lists.newArrayList(userRepository.findOne(1l));
    }

    @Override
    public OrderEntity saveTender(final OrderEntity tender, final UserEntity caller) {
        if (tender == null || !tender.isAllowed(caller)) return tender;
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
        if (order == null || !order.isAllowed(caller)) return order;
        if (order.getType() == OrderEntity.Type.PRIVATE) order.setTotalPrice(order.getProducts().stream().mapToInt(t -> t.getCount() * t.getPrice()).sum());
        return orderRepository.save(order);
    }

    @Override
    public void deleteProduct(final Long oid, final Long pid, final UserEntity caller) {
        final OrderEntity order = orderRepository.findOne(oid);
        final OrderProductEntity product = orderProductRepository.findOne(pid);
        if (order == null || !order.isAllowed(caller)) return;
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
        if (order == null || !order.isAllowed(caller)) return;
        final UserEntity customer = order.getCustomer();
        final UserEntity referrer = customer.getReferrer();
        order.setStatus(status);
        orderRepository.save(order);
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("ordernumber", order.getOrderNumber());
                put("status", status);
                put("address", order.getOrderUrl());
                put("order", order);
            }
        };
        if (status == Status.CLOSED) {
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
            mailService.sendMailMessage(customer, MailService.CLOSE_ORDER_CUSTOMER, params);
            params.put("username", order.getCatalog().getUser().getFullName());
            mailService.sendMailMessage(order.getCatalog().getUser(), MailService.CLOSE_ORDER_COOK, params);
        } else {
            params.put("username", customer.getFullName());
            mailService.sendMailMessage(customer, MailService.EDIT_ORDER, params);
            params.put("username", order.getCatalog().getUser().getFullName());
            mailService.sendMailMessage(order.getCatalog().getUser(), MailService.EDIT_ORDER, params);
        }
    }

    @Override
    public List<OrderProductEntity> findAllItems(final OrderEntity order) {
        return orderProductRepository.findAllByOrder(order);
    }

    @Override
    public ImageWithObject<OrderEntity, PhotoEntity> processUploadedImages(String objectId, Map<ImageSize, String> imageUrls) {

        checkNotNull(objectId, "ObjectId should not be null");
        final OrderEntity order = orderRepository.findOne(Long.parseLong(objectId));
        checkNotNull(order, "Order should not be null");

        final PhotoEntity photo = new PhotoEntity();
        photo.setType(PhotoEntity.Type.ORDER);
        photo.setOrder(order);
        photo.setAvatarUrlSmall(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE1), photo.getAvatarUrlSmall()));
        photo.setAvatarUrl(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE2), photo.getAvatarUrl()));
        photo.setAvatarUrlBig(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE3), photo.getAvatarUrlBig()));
        photoRepository.save(photo);

        return new ImageWithObject<>(order, photo);
    }

    @Override
    public void run() {
        try {
            logger.debug("Check for trigger send");
            final DateTime now = DateTime.now();
            findAllTenders().stream()
                    .filter(t -> t.getStatus() == Status.NEW)
                    .filter(t -> t.getCatalog() == null)
                    .filter(t -> !t.isTenderExpired())
                    .forEach(tender -> {
                        final DateTime tenderTime = new DateTime(tender.getDate());
                        final DateTime lastTrigger = new DateTime(tender.getTriggerTime() == null ? tender.getDate() : tender.getTriggerTime());
                        Lists.newArrayList(tenderTime.plus(Period.days(3)),
                                           tenderTime.plus(Period.days(1)),
                                           tenderTime.plus(Period.minutes(45)))
                                .stream()
                                .filter(now::isAfter)
                                .filter(lastTrigger::isBefore)
                                .findFirst()
                                .ifPresent(period -> triggerTenderEmail(tender, period));
                    });
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    @Override
    public void triggerTenderEmail(OrderEntity tender, DateTime period) {
        transactionTemplate.execute(status -> {
            final OrderEntity localTender = orderRepository.findOne(tender.getId());
            final List<CommentEntity> replies = ratingService.findAllComments(localTender);
            if (!replies.isEmpty()) {
                logger.info("Fire trigger {} for tender {}", period, localTender);
                localTender.setTriggerTime(new Date());
                orderRepository.save(localTender);
                final Map<String, Object> params = new HashMap<String, Object>() {
                    {
                        put("username", localTender.getCustomer().getFullName());
                        put("address", localTender.getOrderUrl());
                        put("tender", localTender);
                        put("replies", replies);
                    }
                };
                mailService.sendMailMessage(localTender.getCustomer(), MailService.TENDER_REMINDER, params);
            }
            return null;
        });
    }

}
