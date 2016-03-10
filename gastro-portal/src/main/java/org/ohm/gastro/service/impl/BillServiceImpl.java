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
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.trait.Logging;
import org.ohm.gastro.util.CommonsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final MailService mailService;
    private final Date firstPeriod;

    @Autowired
    public BillServiceImpl(final BillRepository billRepository, final OrderRepository orderRepository, final MailService mailService) {
        this.billRepository = billRepository;
        this.orderRepository = orderRepository;
        this.mailService = mailService;
        Date localfirstPeriod;
        try {
            localfirstPeriod = CommonsUtils.GUI_DATE.get().parse("01/10/2015");
        } catch (ParseException e) {
            logger.error("", e);
            localfirstPeriod = new Date();
        }
        firstPeriod = localfirstPeriod;
    }

    @Override
    public List<BillEntity> findAllBills(final CatalogEntity catalog) {
        return billRepository.findAllByCatalogOrderByBillNumber(catalog);
    }

    @Override
    public List<BillEntity> findAllBills(Date period) {
        return billRepository.findAllByDate(period, new DateTime(period).plusMonths(1).toDate());
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

    @Override
    public boolean checkPayment(final Long billId, final Float amount) {
        final BillEntity bill = billRepository.findOne(billId);
        if (bill != null && bill.getStatus() == Status.UNPAID) {
            logger.info("Checking payment for bill {}, amount {}, bill fee {}", bill, amount, bill.getFee());
            return bill.getFee() == amount.intValue();
        }
        return false;
    }

    @Override
    public List<Date> findPeriods() {
        final DateTime from = new DateTime(firstPeriod);
        final DateTime to = new DateTime();
        final int months = Months.monthsBetween(from, to).getMonths();
        return IntStream.range(0, months + 1)
                .mapToObj(t -> from.plusMonths(months - t).dayOfMonth().roundFloorCopy().toDate())
                .collect(Collectors.toList());
    }

    @Override
    public boolean proceedPayment(final Long billId) {
        final BillEntity bill = billRepository.findOne(billId);
        if (bill != null && bill.getStatus() == Status.UNPAID) {
            logger.info("Bill {} was successfully paid", bill);
            bill.setStatus(Status.PAID);
            billRepository.save(bill);
            try {
                final Map<String, Object> params = new HashMap<String, Object>() {
                    {
                        put("bill", bill);
                        put("catalog", bill.getCatalog());
                    }
                };
                mailService.sendAdminMessage(MailService.MailType.NEW_BILL_ADMIN, params);
            } catch (MailException e) {
                logger.error("", e);
            }
            return true;
        }
        return false;
    }

}
