package org.ohm.gastro.service.impl;

import org.apache.commons.lang.StringUtils;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.LogEntity.Type;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.CatalogRepository;
import org.ohm.gastro.reps.CommentRepository;
import org.ohm.gastro.reps.OrderProductRepository;
import org.ohm.gastro.reps.OrderRepository;
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.service.OrderService;
import org.ohm.gastro.service.RatingModifier;
import org.ohm.gastro.service.RatingService;
import org.ohm.gastro.service.RatingTarget;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

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
    private final CommentRepository commentRepository;
    private final RatingService ratingService;

    @Autowired
    public OrderServiceImpl(final OrderRepository orderRepository,
                            final OrderProductRepository orderProductRepository,
                            final CatalogRepository catalogRepository,
                            final MailService mailService,
                            final CommentRepository commentRepository,
                            final RatingService ratingService) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.catalogRepository = catalogRepository;
        this.mailService = mailService;
        this.commentRepository = commentRepository;
        this.ratingService = ratingService;
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
            return order;
        }
        return null;
        //        try {
        //            orders.stream().forEach(order -> {
        //                if (!items.isEmpty()) {
        //                    final CatalogEntity catalog = catalogRepository.findOne(order.getProducts().get(0).getEntity().getCatalog().getId());
        //                    final Map<String, Object> params = new HashMap<String, Object>() {
        //                        {
        //                            put("products", order.getProducts());
        //                            put("ordernumber", order.getOrderNumber());
        //                            put("customer", customer);
        //                            put("customer_email", eMail);
        //                            put("comment", defaultIfNull(order.getComment(), ""));
        //                            put("cook", catalog);
        //                            put("total", order.getOrderTotalPrice());
        //                            put("hasBonuses", order.getUsedBonuses() > 0);
        //                            put("bonuses", order.getUsedBonuses());
        //                        }
        //                    };
        //                    mailService.sendAdminMessage(MailService.NEW_ORDER_ADMIN, params);
        //                    mailService.sendMailMessage(catalog.getUser().getEmail(), MailService.NEW_ORDER_COOK, params);
        //                    mailService.sendMailMessage(eMail, MailService.NEW_ORDER_CUSTOMER, params);
        //                }
        //            });
        //        } catch (MailException e) {
        //            logger.error("", e);
        //        }
    }

    @Override
    public List<OrderEntity> findAllOrders(final UserEntity customer, final CatalogEntity catalog) {
        return orderRepository.findAllByCustomerAndType(customer, OrderEntity.Type.PRIVATE);
    }

    @Override
    public List<OrderEntity> findAllOrders(final CatalogEntity catalog) {
        return orderRepository.findAllByCatalogAndType(catalog, OrderEntity.Type.PRIVATE);
    }

    @Override
    public List<OrderEntity> findAllOrders(final CatalogEntity catalog, final Status status) {
        return orderRepository.findAllByCatalogAndStatusAndType(catalog, status, OrderEntity.Type.PRIVATE);
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
        tender.setStatus(Status.ACTIVE);
        orderRepository.save(tender);
        tender.setOrderNumber(Long.toString(tender.getId()));
        return tender;
    }

    @Override
    public void placeReply(final OrderEntity order, final UserEntity cook, final String replyText) {
        if (cook.isCook() && StringUtils.isNotEmpty(replyText)) {
            final CommentEntity reply = new CommentEntity();
            reply.setType(CommentEntity.Type.ORDER);
            reply.setOrder(order);
            reply.setAuthor(cook);
            reply.setText(replyText);
            reply.setDate(new Date());
            commentRepository.save(reply);
        }
    }

    @Override
    public OrderEntity saveTender(final OrderEntity tender, final UserEntity caller) {
        if (tender == null || !tender.isAllowed(caller)) return tender;
        return orderRepository.save(tender);
    }

    @Override
    public OrderEntity findOrder(final Long id) {
        return id == null ? null : orderRepository.findOne(id);
    }

    @Override
    public void saveOrder(final OrderEntity order, final UserEntity caller) {
        if (order == null || !order.isAllowed(caller)) return;
        order.setTotalPrice(order.getProducts().stream().mapToInt(t -> t.getCount() * t.getPrice()).sum());
        orderRepository.save(order);
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
        order.setTotalPrice(order.getProducts().stream().mapToInt(t -> t.getCount() * t.getPrice()).sum());
        orderRepository.save(order);
    }

    @Override
    @RatingModifier
    public void changeStatus(final OrderEntity order, final Status status, @RatingTarget final CatalogEntity catalog, final UserEntity caller) {
        if (order == null || !order.isAllowed(caller)) return;
        if (status == Status.CLOSED) {
            ratingService.registerEvent(Type.ORDER_DONE, catalog, order.getTotalPrice());
        }
        order.setStatus(status);
        orderRepository.save(order);
//        final Map<String, Object> params = new HashMap<String, Object>() {
//            {
//                put("ordernumber", order.getOrderNumber());
//                put("catalog", catalog);
//                put("status", status);
//            }
//        };
//        mailService.sendAdminMessage(MailService.EDIT_ORDER, params);
//        if (order.getCustomer().getEmail() != null) {
//            mailService.sendMailMessage(order.getCustomer().getEmail(), MailService.EDIT_ORDER, params);
//        }
    }

    @Override
    public List<OrderProductEntity> findAllItems(final OrderEntity order) {
        return orderProductRepository.findAllByOrder(order);
    }

}
