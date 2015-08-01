package org.ohm.gastro.service.impl;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.LogEntity.Type;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.CatalogRepository;
import org.ohm.gastro.reps.OrderProductRepository;
import org.ohm.gastro.reps.OrderRepository;
import org.ohm.gastro.reps.UserRepository;
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.service.OrderService;
import org.ohm.gastro.service.RatingModifier;
import org.ohm.gastro.service.RatingService;
import org.ohm.gastro.service.RatingTarget;
import org.ohm.gastro.trait.Logging;
import org.ohm.gastro.util.CommonsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang.ObjectUtils.defaultIfNull;

/**
 * Created by ezhulkov on 12.10.14.
 */
@Component("orderService")
@Transactional
public class OrderServiceImpl implements OrderService, Logging {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final UserRepository userRepository;
    private final CatalogRepository catalogRepository;
    private final MailService mailService;
    private final RatingService ratingService;

    @Autowired
    public OrderServiceImpl(final OrderRepository orderRepository,
                            final OrderProductRepository orderProductRepository,
                            final UserRepository userRepository,
                            final CatalogRepository catalogRepository,
                            final MailService mailService,
                            final RatingService ratingService) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.userRepository = userRepository;
        this.catalogRepository = catalogRepository;
        this.mailService = mailService;
        this.ratingService = ratingService;
    }

    @Override
    public List<OrderEntity> placeOrder(OrderEntity totalOrder, List<OrderProductEntity> purchaseItems, final UserEntity customer, final String eMail) {
        final Integer totalPrice = purchaseItems.stream().mapToInt(t -> t.getCount() * t.getPrice()).sum();
        userRepository.save(customer);
        List<OrderEntity> orders = purchaseItems.stream()
                .collect(Collectors.groupingBy(t -> t.getEntity().getCatalog())).entrySet().stream()
                .map(t -> {
                    final CatalogEntity catalog = catalogRepository.findOne(t.getKey().getId());
                    final List<OrderProductEntity> products = t.getValue();
                    final OrderEntity order = new OrderEntity();
                    order.setComment(totalOrder.getComment());
                    order.setDate(new Timestamp(System.currentTimeMillis()));
                    order.setCustomer(totalOrder.getCustomer());
                    order.setProducts(products);
                    order.setUsedBonuses(Math.min(totalOrder.getUsedBonuses() * order.getOrderTotalPrice() / totalPrice, order.getOrderTotalPrice()));
                    order.setStatus(Status.NEW);
                    orderRepository.save(order);
                    products.stream().forEach(p -> p.setOrder(order));
                    orderProductRepository.save(products);
                    order.setOrderNumber(CommonsUtils.ORDER_DATE.get().format(new Date(System.currentTimeMillis())) + "-" + order.getId());
                    return orderRepository.save(order);
                })
                .collect(Collectors.toList());

        try {
            orders.stream().forEach(order -> {
                if (!order.getProducts().isEmpty()) {
                    final CatalogEntity catalog = catalogRepository.findOne(order.getProducts().get(0).getEntity().getCatalog().getId());
                    final Map<String, Object> params = new HashMap<String, Object>() {
                        {
                            put("products", order.getProducts());
                            put("ordernumber", order.getOrderNumber());
                            put("customer", customer);
                            put("customer_email", eMail);
                            put("comment", defaultIfNull(order.getComment(), ""));
                            put("cook", catalog);
                            put("total", order.getOrderTotalPrice());
                            put("hasBonuses", order.getUsedBonuses() > 0);
                            put("bonuses", order.getUsedBonuses());
                        }
                    };
                    mailService.sendAdminMessage(MailService.NEW_ORDER_ADMIN, params);
                    mailService.sendMailMessage(catalog.getUser().getEmail(), MailService.NEW_ORDER_COOK, params);
                    mailService.sendMailMessage(eMail, MailService.NEW_ORDER_CUSTOMER, params);
                }
            });
        } catch (MailException e) {
            logger.error("", e);
        }

        return orders;
    }

    @Override
    public List<OrderEntity> findAllOrders(final UserEntity customer, final CatalogEntity catalog) {
//        return orderRepository.findAllByCatalogAndCustomer(customer, catalog); todo
        return Lists.newArrayList();
    }

    @Override
    public List<OrderEntity> findAllOrders(final CatalogEntity catalog) {
//        return orderRepository.findAllByCatalog(catalog, null); todo
        return Lists.newArrayList();
    }

    @Override
    public List<OrderEntity> findAllOrders(final CatalogEntity catalog, final Status status) {
//        return orderRepository.findAllByCatalog(catalog, status); todo
        return Lists.newArrayList();
    }

    @Override
    public int getBonuses(int price) {
        return (int) Math.ceil(price * 0.03);
    }

    @Override
    public OrderEntity findOrder(final Long id) {
        return orderRepository.findOne(id);
    }

    @Override
    public void saveOrder(final OrderEntity order) {
        orderRepository.save(order);
    }

    @Override
    public void deleteProduct(final Long oid, final Long pid) {
        final OrderEntity order = orderRepository.findOne(oid);
        final OrderProductEntity product = orderProductRepository.findOne(pid);
        order.getProducts().remove(product);
        orderProductRepository.delete(product);
        orderRepository.save(order);
    }

    @Override
    public void incProduct(final Long oid, final Long pid) {
        final OrderProductEntity product = orderProductRepository.findOne(pid);
        product.setCount(product.getCount() + 1);
        orderProductRepository.save(product);
    }

    @Override
    public void decProduct(final Long oid, final Long pid) {
        final OrderProductEntity product = orderProductRepository.findOne(pid);
        product.setCount(Math.max(1, product.getCount() - 1));
        orderProductRepository.save(product);
    }

    @Override
    @RatingModifier
    public void changeStatus(final OrderEntity order, final Status status, @RatingTarget final CatalogEntity catalog) {
        if (status == Status.CANCELLED) order.setUsedBonuses(0);
        if (status == Status.READY) {
            final UserEntity customer = order.getCustomer();
            if (StringUtils.isNotEmpty(customer.getEmail())) {
                if (order.getUsedBonuses() > 0) {
                    customer.setBonus(Math.max(0, customer.getBonus() - order.getUsedBonuses()));
                } else {
                    customer.setBonus((int) (customer.getBonus() + getBonuses(order.getOrderTotalPrice())));
                }
                userRepository.save(customer);
            }
            ratingService.registerEvent(Type.ORDER_DONE, catalog, order.getOrderTotalPrice());
        }
        order.setStatus(status);
        orderRepository.save(order);
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("ordernumber", order.getOrderNumber());
                put("catalog", catalog);
                put("status", status);
            }
        };
        mailService.sendAdminMessage(MailService.EDIT_ORDER, params);
        if (order.getCustomer().getEmail() != null) {
            mailService.sendMailMessage(order.getCustomer().getEmail(), MailService.EDIT_ORDER, params);
        }
    }

}
