package org.ohm.gastro.service.impl;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.apache.commons.lang.ObjectUtils;
import org.ohm.gastro.domain.CatalogEntity;
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

import javax.annotation.PreDestroy;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.scribe.utils.Preconditions.checkNotNull;

/**
 * Created by ezhulkov on 12.10.14.
 */
@Component("orderService")
@Transactional
@ImageUploader(FileType.ORDER)
public class OrderServiceImpl implements OrderService, Logging {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final CatalogRepository catalogRepository;
    private final ImageRepository photoRepository;
    private final MailService mailService;
    private final UserRepository userRepository;
    private final RatingService ratingService;
    private final List<String> filterEmails = Lists.newArrayList("jazzcook@yandex.ru", "cook@cook.com", "cook@cook.ru");
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    public OrderServiceImpl(final OrderRepository orderRepository,
                            final OrderProductRepository orderProductRepository,
                            final CatalogRepository catalogRepository,
                            final ImageRepository photoRepository,
                            final MailService mailService,
                            final UserRepository userRepository,
                            final RatingService ratingService) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.catalogRepository = catalogRepository;
        this.photoRepository = photoRepository;
        this.mailService = mailService;
        this.userRepository = userRepository;
        this.ratingService = ratingService;
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }

    @Override
    public OrderEntity placeOrder(final OrderEntity preOrder) {
        if (!preOrder.getProducts().isEmpty()) {
            final OrderEntity order = new OrderEntity();
            order.setDate(new Timestamp(System.currentTimeMillis()));
            order.setCustomer(preOrder.getCustomer());
            order.setDueDate(preOrder.getDueDate());
            order.setPromoCode(preOrder.getPromoCode());
            order.setComment(preOrder.getComment());
            order.setProducts(preOrder.getProducts());
            order.setStatus(Status.ACTIVE);
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
                    }
                };
                mailService.sendAdminMessage(MailService.NEW_ORDER_ADMIN, params);
                params.put("username", order.getCatalog().getUser().getFullName());
                mailService.sendMailMessage(order.getCatalog().getUser().getEmail(), MailService.NEW_ORDER_COOK, params);
                params.put("username", order.getCustomer().getFullName());
                mailService.sendMailMessage(order.getCustomer().getEmail(), MailService.NEW_ORDER_CUSTOMER, params);
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
    public List<OrderEntity> findAllTenders() {
        return orderRepository.findAllByType(OrderEntity.Type.PUBLIC);
    }

    @Override
    public OrderEntity placeTender(final OrderEntity tender, final UserEntity caller) {
        if (tender == null || !tender.isAllowed(caller)) return tender;
        tender.setDate(new Timestamp(System.currentTimeMillis()));
        tender.setType(OrderEntity.Type.PUBLIC);
        tender.setStatus(Status.NEW);
        orderRepository.save(tender);
        tender.setOrderNumber(Long.toString(tender.getId()));
        return orderRepository.save(tender);
    }

    @Override
    public OrderEntity commitTender(OrderEntity tender, UserEntity caller) {
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("username", tender.getCustomer().getFullName());
                put("customer", tender.getCustomer());
                put("name", ObjectUtils.defaultIfNull(tender.getName(), ""));
                put("comment", ObjectUtils.defaultIfNull(tender.getComment(), ""));
                put("total", ObjectUtils.defaultIfNull(tender.getTotalPrice(), ""));
                put("date", ObjectUtils.defaultIfNull(tender.getDatePrintable(), "-"));
                put("address", tender.getOrderUrl());
            }
        };
//        final List<UserEntity> rcpts = catalogRepository.findAll().stream().
//                map(CatalogEntity::getUser).distinct().
//                filter(t -> !filterEmails.contains(t.getEmail())).collect(Collectors.toList());
        final List<UserEntity> rcpts = Lists.newArrayList(userRepository.findOne(1l));
        executorService.execute(() -> {
            try {
                mailService.sendAdminMessage(MailService.NEW_TENDER_ADMIN, params);
                mailService.sendMailMessage(tender.getCustomer().getEmail(), MailService.NEW_TENDER_CUSTOMER, params);
                rcpts.forEach(cook -> {
                    params.put("username", cook.getFullName());
                    params.put("email", cook.getEmail());
                    mailService.sendMailMessage(cook.getEmail(), MailService.NEW_TENDER_COOK, params);
                });
            } catch (MailException e) {
                logger.error("", e);
            }
        });
        return tender;
    }

    @Override
    public OrderEntity attachTender(CatalogEntity catalog, OrderEntity order, UserEntity caller) {
        if (order.getCatalog() != null) return order;
        order.setCatalog(catalog);
        order.setStatus(Status.CONFIRMED);
        saveOrder(order, caller);
        try {
            final Map<String, Object> params = new HashMap<String, Object>() {
                {
                    put("address", order.getOrderUrl());
                }
            };
            params.put("username", catalog.getUser().getFullName());
            mailService.sendMailMessage(catalog.getUser().getEmail(), MailService.TENDER_ATTACHED_COOK, params);
            params.put("username", order.getCustomer().getFullName());
            mailService.sendMailMessage(order.getCustomer().getEmail(), MailService.TENDER_ATTACHED_CUSTOMER, params);
        } catch (MailException e) {
            logger.error("", e);
        }
        return order;
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
        }
        order.setStatus(status);
        orderRepository.save(order);
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("ordernumber", order.getOrderNumber());
                put("status", status);
                put("address", order.getOrderUrl());
            }
        };
        params.put("username", customer.getFullName());
        mailService.sendMailMessage(customer.getEmail(), MailService.EDIT_ORDER, params);
        params.put("username", order.getCatalog().getUser().getFullName());
        mailService.sendMailMessage(order.getCatalog().getUser().getEmail(), MailService.EDIT_ORDER, params);
    }

    @Override
    public List<OrderProductEntity> findAllItems(final OrderEntity order) {
        return orderProductRepository.findAllByOrder(order);
    }

    @Override
    public OrderEntity processUploadedImages(String objectId, Map<ImageSize, String> imageUrls) {

        checkNotNull(objectId, "ObjectId should not be null");
        final OrderEntity order = orderRepository.findOne(Long.parseLong(objectId));
        checkNotNull(order, "Order should not be null");

        final PhotoEntity photo = new PhotoEntity();
        photo.setType(PhotoEntity.Type.ORDER);
        photo.setOrder(order);
        photo.setUrlSmall(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE1), photo.getUrlSmall()));
        photo.setUrl(Objects.firstNonNull(imageUrls.get(ImageSize.SIZE1), photo.getUrl()));
        photoRepository.save(photo);

        return order;
    }

}
