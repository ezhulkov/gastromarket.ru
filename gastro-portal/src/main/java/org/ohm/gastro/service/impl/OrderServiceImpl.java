package org.ohm.gastro.service.impl;

import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.ohm.gastro.domain.AbstractBaseEntity;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.LogEntity.Type;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.domain.Region;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.filter.RegionFilter;
import org.ohm.gastro.reps.CatalogRepository;
import org.ohm.gastro.reps.OrderProductRepository;
import org.ohm.gastro.reps.OrderRepository;
import org.ohm.gastro.reps.PhotoRepository;
import org.ohm.gastro.reps.UserRepository;
import org.ohm.gastro.service.ConversationService;
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.service.MailService.MailType;
import org.ohm.gastro.service.OrderService;
import org.ohm.gastro.service.PhotoService;
import org.ohm.gastro.service.RatingModifier;
import org.ohm.gastro.service.RatingService;
import org.ohm.gastro.service.RatingTarget;
import org.ohm.gastro.trait.Logging;
import org.ohm.gastro.util.CommonsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

/**
 * Created by ezhulkov on 12.10.14.
 */
@Component("orderService")
@Transactional
public class OrderServiceImpl implements OrderService, Logging {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final CatalogRepository catalogRepository;
    private final MailService mailService;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final RatingService ratingService;
    private final PhotoService photoService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ConversationService conversationService;

    @Autowired
    public OrderServiceImpl(final OrderRepository orderRepository,
                            final OrderProductRepository orderProductRepository,
                            final CatalogRepository catalogRepository,
                            final MailService mailService,
                            final UserRepository userRepository,
                            final PhotoRepository photoRepository,
                            final RatingService ratingService,
                            final PhotoService photoService,
                            final ConversationService conversationService) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.catalogRepository = catalogRepository;
        this.mailService = mailService;
        this.userRepository = userRepository;
        this.photoRepository = photoRepository;
        this.ratingService = ratingService;
        this.photoService = photoService;
        this.conversationService = conversationService;
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }

    @Override
    public OrderEntity placeOrder(OrderEntity order, List<PhotoEntity> photos, UserEntity caller, CatalogEntity catalog) {
        order.setDate(new Date());
        order.setCustomer(caller);
        order.setStatus(Status.NEW);
        order.setRegion(RegionFilter.getCurrentRegion());
        order.setWasSetup(true);
        order.setType(OrderEntity.Type.PRIVATE);
        order.setCatalog(catalog);
        order.getPhotos().clear();
        orderRepository.save(order);
        order.getProducts().stream().forEach(p -> p.setOrder(order));
        orderProductRepository.save(order.getProducts());
        order.setOrderNumber(Long.toString(order.getId()));
        photoService.attachPhotos(order, photos);
        logger.info("New private order received {}, customer {}, catalog {}, photos {}", order, order.getCustomer(), order.getCatalog(), order.getPhotos());
        try {
            final Map<String, Object> params = new HashMap<String, Object>() {
                {
                    put("products", order.getProducts());
                    put("catalog", order.getCatalog());
                    put("customer", order.getCustomer());
                    put("comment", defaultIfNull(order.getComment(), "-"));
                    put("cook", order.getCatalog());
                    put("total", order.getTotalPrice());
                    put("address", order.getOrderUrl());
                    put("region", order.getRegion());
                    put("order", order);
                    put("photos", photoRepository.findAll(photos.stream().map(AbstractBaseEntity::getId).collect(Collectors.toList())));
                    put("date", defaultIfNull(order.getDueDateAsString(), "-"));
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

    @Override
    public List<OrderEntity> findAllOrders() {
        return orderRepository.findAll(new Sort(Direction.DESC, "date"));
    }

    @Override
    public List<OrderEntity> findAllOrders(final UserEntity customer) {
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
    public void confirmOrder(OrderEntity order) {
        order.setStatus(Status.ACTIVE);
        saveOrder(order);
        logger.info("Confirming order {}, total price {}", order, order.getTotalPrice());
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("order", order);
                put("address", order.getOrderUrl());
                put("customer", order.getCustomer());
                put("ordername", order.getOrderName());
            }
        };
        mailService.sendAdminMessage(MailType.CONFIRM_ORDER_ADMIN, params);
        params.put("username", order.getCustomer().getFullName());
        mailService.sendMailMessage(order.getCustomer(), MailType.CONFIRM_ORDER_CUSTOMER, params);
        params.put("username", order.getCatalog().getUser().getFullName());
        mailService.sendMailMessage(order.getCatalog().getUser(), MailType.CONFIRM_ORDER_COOK, params);
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
                put("ordername", order.getOrderName());
                put("reason", defaultIfNull(order.getCancelReason(), "-"));
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
        if (order.getCatalog() != null) {
            params.put("username", order.getCatalog().getUser().getFullName());
            mailService.sendMailMessage(order.getCatalog().getUser(), MailType.CANCEL_ORDER_COOK, params);
        }
    }

    @Override
    public void closeOrder(final OrderEntity order, final int totalPrice, final int deliveryPrice, final String survey, final UserEntity caller) {
        logger.info("Closing order {}, survey {}", order, survey);
        final Map<String, Object> params = new HashMap<String, Object>() {{
            put("address", order.getOrderUrl());
            put("order", order);
            put("survey", survey);
            put("price", totalPrice);
            put("deliveryPrice", deliveryPrice);
            put("priceWas", order.getTotalPrice());
            put("deliveryPriceWas", order.getDeliveryPrice());
        }};
        if (totalPrice != order.getTotalPrice()) {
            logger.info("Price changed. Was {}, become {}", order.getTotalPrice(), totalPrice);
            order.setTotalPrice(totalPrice);
        }
        if (order.getDeliveryPrice() != null && deliveryPrice != order.getDeliveryPrice()) {
            logger.info("Delivery price changed. Was {}, become {}", order.getDeliveryPrice(), deliveryPrice);
            order.setDeliveryPrice(deliveryPrice);
        }
        mailService.sendAdminMessage(MailService.MailType.CLOSE_ORDER_ADMIN, params);
        changeStatusInternal(order, Status.CLOSED, order.getCatalog(), caller, survey);
    }

    @Override
    public List<OrderEntity> findCommonOrders(final UserEntity author, final UserEntity opponent) {
        final UserEntity cook = author.isCook() ? author : opponent.isCook() ? opponent : null;
        final UserEntity user = author.equals(cook) ? opponent : author;
        final List<OrderEntity> commonOrders = CommonsUtils.coalesceEmptyLazy(orderRepository.findAllByCookAndCustomer(cook, user).stream()
                                                                                      .filter(t -> !t.isOrderClosed())
                                                                                      .collect(Collectors.toList()),
                                                                              () -> findCommonComments(author, opponent).stream()
                                                                                      .filter(t -> t.getEntity() instanceof OrderEntity)
                                                                                      .map(t -> (OrderEntity) t.getEntity())
                                                                                      .collect(Collectors.toList()));
        if (commonOrders != null) Collections.sort(commonOrders, (o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        return commonOrders == null ? Lists.newArrayList() : commonOrders;
    }

    @Override
    public List<CommentEntity> findCommonComments(final UserEntity author, final UserEntity opponent) {
        final UserEntity cook = author.isCook() ? author : opponent.isCook() ? opponent : null;
        final UserEntity user = author.equals(cook) ? opponent : author;
        if (cook == null || user == null) return Lists.newArrayList();
        return conversationService.findAllCommentsByAuthor(cook).stream()
                .map(t -> {
                    Hibernate.initialize(t);
                    return t;
                })
                .filter(t -> t.getEntity() instanceof OrderEntity && ((OrderEntity) t.getEntity()).getCustomer().equals(user))
                .sorted(((o1, o2) -> o2.getDate().compareTo(o1.getDate())))
                .collect(Collectors.toList());
    }

    @Override
    public void processCateringRequest(final String fullName, final String mobile, final String eMail, final String comment) {
        logger.info("Catering received");
        logger.info("fullName: {}", fullName);
        logger.info("mobile: {}", mobile);
        logger.info("email: {}", eMail);
        logger.info("comment: {}", comment);

        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("fullName", defaultIfNull(fullName, ""));
                put("mobile", defaultIfNull(mobile, ""));
                put("email", defaultIfNull(eMail, ""));
                put("comment", defaultIfNull(comment, ""));
            }
        };
        mailService.sendAdminMessage(MailType.NEW_CATERING, params);
        mailService.sendMailMessage("event@gastromarket.ru", MailType.NEW_CATERING, params);
    }

    @Override
    public List<OrderEntity> findAllTenders(Region region) {
        return orderRepository.findAllByType(OrderEntity.Type.PUBLIC).stream()
                .filter(OrderEntity::isWasSetup)
                .filter(t -> t.getRegion() == region)
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
                put("comment", defaultIfNull(tender.getComment(), "-"));
                put("total", defaultIfNull(tender.getTotalPrice(), "-"));
                put("date", defaultIfNull(tender.getDueDateAsString(), "-"));
                put("region", tender.getRegion());
                put("address", tender.getOrderUrl());
                put("tender", tender);
                put("photos", photoRepository.findAll(tender.getPhotos().stream().map(AbstractBaseEntity::getId).collect(Collectors.toList())));
                put("recipients", getRecipients(defaultIfNull(tender.getRegion(), Region.DEFAULT)));
            }
        };
        mailService.sendAdminMessage(MailService.MailType.NEW_TENDER_ADMIN, params);
        return tender;
    }

    @Override
    public OrderEntity attachTender(CatalogEntity catalog, OrderEntity order, UserEntity caller) {
        if (order.getCatalog() != null) return order;
        order.setCatalog(catalog);
        order.setStatus(Status.ACTIVE);
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
        final Region region = defaultIfNull(tender.getRegion(), Region.DEFAULT);
        tender.setWasSetup(true);
        tender.setAnnonceSent(true);
        orderRepository.save(tender);
        final List<UserEntity> rcpt = getRecipients(region);
        final List<PhotoEntity> photos = photoRepository.findAllByOrder(tender);
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("username", tender.getCustomer().getFullName());
                put("customer", tender.getCustomer());
                put("total", tender.getTotalPrice());
                put("comment", defaultIfNull(tender.getComment(), "-"));
                put("date", defaultIfNull(tender.getDueDateAsString(), "-"));
                put("address", tender.getOrderUrl());
                put("tender", tender);
                put("cooks", rcpt);
                put("photos", photoRepository.findAll(photos.stream().map(AbstractBaseEntity::getId).collect(Collectors.toList())));
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

    private void changeStatusInternal(final OrderEntity order, final Status status, @RatingTarget final CatalogEntity catalog, final UserEntity caller, final String survey) {

        if (order == null || !order.isAccessAllowed(caller)) return;
        final UserEntity customer = order.getCustomer();
        final UserEntity referrer = customer.getReferrer();
        order.setStatus(status);

        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("status", status);
                put("products", order.getProducts());
                put("customer", order.getCustomer());
                put("comment", defaultIfNull(order.getComment(), "-"));
                put("cook", order.getCatalog());
                put("total", order.getTotalPrice());
                put("address", order.getOrderUrl());
                put("region", order.getRegion());
                put("order", order);
                put("survey", survey);
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
        } else {
            params.put("username", customer.getFullName());
            mailService.sendMailMessage(customer, MailService.MailType.EDIT_ORDER, params);
            params.put("username", order.getCatalog().getUser().getFullName());
            mailService.sendMailMessage(order.getCatalog().getUser(), MailService.MailType.EDIT_ORDER, params);
        }
        orderRepository.save(order);
    }

}
