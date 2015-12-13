package org.ohm.gastro.gui.pages.office.bills;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.BillEntity;
import org.ohm.gastro.domain.BillEntity.Status;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.text.NumberFormat;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Index extends BaseComponent {

    @Property
    private BillEntity bill;

    @Property
    private OrderEntity order;

    @Property
    private boolean test = false;

    private final ThreadLocal<NumberFormat> numberFormat = new ThreadLocal<NumberFormat>() {
        @Override
        public NumberFormat get() {
            return NumberFormat.getCurrencyInstance();
        }
    };

    public void onActivate(Long id, boolean test) {
        onActivate(id);
        this.test = test;
    }

    public void onActivate(Long id) {
        bill = getBillService().findBill(id);
    }

    public Object onPassivate() {
        return bill == null ? null : bill.getId();
    }

    public String getBillTotalOrdersSum() {
        return bill.getTotalOrdersSum() == 0 ? "-" : numberFormat.get().format(bill.getTotalOrdersSum());
    }

    public String getOrderTotalPrice() {
        return order.getTotalPrice() == null ? "-" : numberFormat.get().format(order.getTotalPrice());
    }

    public String getOrderStatus() {
        return getMessages().get(order.getStatus().name());
    }

    public boolean isUnpaid() {
        return bill.getStatus() == Status.UNPAID;
    }

    @Cached
    public String getBillFee() {
        return bill.getFee() == 0 ? "-" : numberFormat.get().format(bill.getFee());
    }

    public String getBillStatus() {
        return getMessages().get("BILL2." + bill.getStatus().name());
    }

    @Cached(watch = "bill")
    public java.util.List<OrderEntity> getOrders() {
        return Stream.concat(getBillService().findClosedOrders(bill).stream(), getBillService().findOpenedOrders(bill).stream()).collect(Collectors.toList());
    }

}
