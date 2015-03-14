package org.ohm.gastro.service.impl;

import org.apache.commons.lang.time.DateUtils;
import org.ohm.gastro.domain.BillEntity;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.BillRepository;
import org.ohm.gastro.reps.CatalogRepository;
import org.ohm.gastro.reps.OrderProductRepository;
import org.ohm.gastro.reps.OrderRepository;
import org.ohm.gastro.reps.UserRepository;
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.service.OrderService;
import org.ohm.gastro.trait.Logging;
import org.ohm.gastro.util.CommonsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Calendar;
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
public class OrderServiceImpl implements OrderService, Logging {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final UserRepository userRepository;
    private final BillRepository billRepository;
    private final CatalogRepository catalogRepository;
    private final MailService mailService;

    @Autowired
    public OrderServiceImpl(final OrderRepository orderRepository, final OrderProductRepository orderProductRepository,
                            final UserRepository userRepository, final BillRepository billRepository,
                            final CatalogRepository catalogRepository, final MailService mailService) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.userRepository = userRepository;
        this.billRepository = billRepository;
        this.catalogRepository = catalogRepository;
        this.mailService = mailService;
    }

    private Date getBillingPeriodStart(Date catalogCreationDate) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(catalogCreationDate);
        final int billingDay = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, billingDay);
        return DateUtils.truncate(calendar, Calendar.DATE).getTime();
    }

    private Date getBillingPeriodEnd(Date catalogCreationDate) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(catalogCreationDate);
        final int billingDay = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, billingDay);
        final Date billingPeriodStart = DateUtils.truncate(calendar, Calendar.DATE).getTime();
        return DateUtils.addMonths(billingPeriodStart, 1);
    }

    @Override
    @Transactional
    public List<OrderEntity> placeOrder(OrderEntity totalOrder, List<OrderProductEntity> purchaseItems, final UserEntity customer, final String eMail) {
        final Integer totalPrice = getProductsPrice(purchaseItems);
        userRepository.save(customer);
        List<OrderEntity> orders = purchaseItems.stream()
                .collect(Collectors.groupingBy(t -> t.getProduct().getCatalog())).entrySet().stream()
                .map(t -> {
                    final CatalogEntity catalog = catalogRepository.findOne(t.getKey().getId());
                    final Date billingPeriodStart = getBillingPeriodStart(catalog.getDate());
                    final Date billingPeriodEnd = getBillingPeriodEnd(catalog.getDate());
                    final List<OrderProductEntity> products = t.getValue();
                    final int orderPrice = getProductsPrice(products);
                    final OrderEntity order = new OrderEntity();
                    BillEntity bill = billRepository.findByCatalogAndDateBetween(catalog, billingPeriodStart, billingPeriodEnd);
                    if (bill == null) {
                        bill = new BillEntity();
                        bill.setDate(billingPeriodStart);
                        bill.setStatus(BillEntity.Status.NEW);
                        bill.setCatalog(catalog);
                        billRepository.save(bill);
                    }
                    order.setUsedBonuses(Math.min(totalOrder.getUsedBonuses() * orderPrice / totalPrice, orderPrice));
                    order.setComment(totalOrder.getComment());
                    order.setDate(new Timestamp(System.currentTimeMillis()));
                    order.setCustomer(totalOrder.getCustomer());
                    order.setProducts(products);
                    order.setBill(bill);
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
                    final CatalogEntity catalog = catalogRepository.findOne(order.getProducts().get(0).getProduct().getCatalog().getId());
                    final Map<String, Object> params = new HashMap<String, Object>() {
                        {
                            put("products", order.getProducts());
                            put("ordernumber", order.getOrderNumber());
                            put("customer", customer);
                            put("customer_email", eMail);
                            put("comment", defaultIfNull(order.getComment(), ""));
                            put("cook", catalog);
                            put("total", order.getTotalPrice());
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
    public List<OrderEntity> findAllOrders(BillEntity bill) {
        return orderRepository.findAllByBill(bill);
    }

    @Override
    public List<BillEntity> findAllBills(CatalogEntity catalog) {
        final List<BillEntity> bills = billRepository.findByCatalogOrderByDateAsc(catalog);
        bills.stream().forEach(bill -> bill.setTotalBill(0));
        return bills;
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
    public void changeStatus(final OrderEntity order, final Status status) {
        if (status == Status.CANCELLED) order.setUsedBonuses(0);
        if (status == Status.READY) {
            final UserEntity customer = order.getCustomer();
            if (order.getUsedBonuses() > 0) {
                customer.setBonus(Math.max(0, customer.getBonus() - order.getUsedBonuses()));
            } else {
                customer.setBonus((int) (customer.getBonus() + Math.ceil(getProductsPrice(order.getProducts()) * 0.03)));
            }
            userRepository.save(customer);
        }
        order.setStatus(status);
        orderRepository.save(order);
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("ordernumber", order.getOrderNumber());
                put("status", status);
            }
        };
        mailService.sendAdminMessage(MailService.NEW_ORDER_ADMIN, params);
        if (order.getCustomer().getEmail() != null) {
            mailService.sendMailMessage(order.getCustomer().getEmail(), MailService.EDIT_ORDER, params);
        }
    }

}
