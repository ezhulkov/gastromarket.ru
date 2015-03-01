package org.ohm.gastro.service.impl;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.OrderProductRepository;
import org.ohm.gastro.reps.OrderRepository;
import org.ohm.gastro.reps.UserRepository;
import org.ohm.gastro.service.OrderService;
import org.ohm.gastro.util.CommonsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 12.10.14.
 */
@Component("orderService")
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, final OrderProductRepository orderProductRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public List<OrderEntity> placeOrder(OrderEntity totalOrder, List<OrderProductEntity> purchaseItems, final UserEntity customer) {
        final Integer totalPrice = getProductsPrice(purchaseItems);
        userRepository.save(customer);
        return purchaseItems.stream()
                .collect(Collectors.groupingBy(t -> t.getProduct().getCatalog())).entrySet().stream()
                .map(t -> {
                    final CatalogEntity catalog = t.getKey();
                    final List<OrderProductEntity> products = t.getValue();
                    final int orderPrice = getProductsPrice(products);
                    final OrderEntity order = new OrderEntity();
                    order.setUsedBonuses(Math.min(totalOrder.getUsedBonuses() * orderPrice / totalPrice, orderPrice));
                    order.setComment(totalOrder.getComment());
                    order.setDate(new Timestamp(System.currentTimeMillis()));
                    order.setCustomer(totalOrder.getCustomer());
                    order.setProducts(products);
                    order.setStatus(Status.NEW);
                    orderRepository.save(order);
                    products.stream().forEach(p -> p.setOrder(order));
                    orderProductRepository.save(products);
                    order.setOrderNumber(CommonsUtils.ORDER_DATE.get().format(new Date(System.currentTimeMillis())) + "-" + order.getId());
                    return orderRepository.save(order);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderEntity> findAllOrders(final UserEntity customer, final CatalogEntity catalog) {
        return orderRepository.findAllByCatalogAndCustomer(customer, catalog);
    }

    @Override
    public List<OrderEntity> findAllOrders(final CatalogEntity catalog) {
        return orderRepository.findAllByCatalog(catalog, null);
    }

    @Override
    public List<OrderEntity> findAllOrders(final CatalogEntity catalog, final Status status) {
        return orderRepository.findAllByCatalog(catalog, status);
    }

    @Override
    public int getProductsPrice(List<OrderProductEntity> products) {
        return products.stream().collect(Collectors.summingInt(t -> t.getPrice() * t.getCount()));
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
    public void changeStatus(final OrderEntity oneOrder, final Status status) {
        if (status == Status.CANCELLED) oneOrder.setUsedBonuses(0);
        if (status == Status.READY) {
            final UserEntity customer = oneOrder.getCustomer();
            if (oneOrder.getUsedBonuses() > 0) {
                customer.setBonus(Math.max(0, customer.getBonus() - oneOrder.getUsedBonuses()));
            } else {
                customer.setBonus((int) (customer.getBonus() + Math.ceil(getProductsPrice(oneOrder.getProducts()) * 0.03)));
            }
            userRepository.save(customer);
        }
        oneOrder.setStatus(status);
        orderRepository.save(oneOrder);
    }

}
