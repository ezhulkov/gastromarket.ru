package org.ohm.gastro.service.impl;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.ohm.gastro.domain.BillEntity;
import org.ohm.gastro.domain.BillEntity.Status;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.reps.BillRepository;
import org.ohm.gastro.reps.OrderRepository;
import org.ohm.gastro.service.BillService;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by ezhulkov on 25.10.15.
 */
@Component
public class BillServiceImpl implements BillService, Logging {

    private final BillRepository billRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public BillServiceImpl(final BillRepository billRepository, final OrderRepository orderRepository) {
        this.billRepository = billRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public List<BillEntity> findAllBills(final CatalogEntity catalog) {
        return billRepository.findAllByCatalogOrderByBillNumber(catalog);
    }

    @Override
    public List<BillEntity> findAllUnpaidBills(final CatalogEntity catalog) {
        return billRepository.findAllByCatalogAndStatusOrderByBillNumber(catalog, Status.UNPAID);
    }

    @Override
    public void createMissingBills(final CatalogEntity catalog) {
        if (catalog == null) return;
        final Optional<BillEntity> lastBill = findAllBills(catalog).stream().reduce((a, b) -> b);
        final DateTime lastBillDate = lastBill.map(BillEntity::getClosingDateAsJoda).orElseGet(catalog::getFirstBillingDate);
        final int lastBillNumber = lastBill.map(BillEntity::getBillNumber).orElse(0);
        final int missingMonths = Math.max(0, Months.monthsBetween(lastBillDate, DateTime.now()).getMonths());
        final List<BillEntity> missingBills = IntStream.rangeClosed(1, missingMonths).mapToObj(t -> {
            final BillEntity bill = new BillEntity();
            bill.setBillNumber(lastBillNumber + t);
            bill.setDate(lastBillDate.plusMonths(t - 1).toDate());
            bill.setCatalog(catalog);
            final List<OrderEntity> closedOrders = findClosedOrders(bill);
            bill.setTotalOrdersSum(closedOrders.stream().mapToInt(OrderEntity::getTotalPrice).sum());
            bill.setStatus(bill.getBillNumber() <= catalog.getFreeMonths() ? Status.FREE : closedOrders.size() == 0 ? Status.EMPTY : Status.UNPAID);
            logger.info("Creating missing bill {} for catalog {}", bill, catalog);
            return bill;
        }).collect(Collectors.toList());
        billRepository.save(missingBills);
    }

    @Override
    public BillEntity findBill(final Long id) {
        return billRepository.findOne(id);
    }

    @Override
    public BillEntity updateBill(final BillEntity bill) {
        return billRepository.save(bill);
    }

    @Override
    public List<OrderEntity> findClosedOrders(final BillEntity bill) {
        return orderRepository.findAllClosedByBill(bill.getCatalog(), bill.getDate(), bill.getClosingDate());
    }

    @Override
    public List<OrderEntity> findOpenedOrders(BillEntity bill) {
        return orderRepository.findAllOpenedByBill(bill.getCatalog(), bill.getDate(), bill.getClosingDate());
    }

}
