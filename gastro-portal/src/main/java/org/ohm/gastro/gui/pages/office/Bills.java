package org.ohm.gastro.gui.pages.office;

import com.google.common.collect.Lists;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.BillEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.text.NumberFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Bills extends BaseComponent {

    @Property
    private BillEntity bill;

    @Property
    private OrderEntity order;

    @Cached
    public List<BillEntity> getBills() {
        return getAuthenticatedUser().getFirstCatalog().map(t -> getBillService().findAllBills(t)).orElseGet(Lists::newLinkedList);
    }

    private final NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

    public String getBillStatus() {
        return getMessages().get("BILL." + bill.getStatus().name());
    }

    public String getBillFee() {
        synchronized (numberFormat) {
            return bill.getFee() == 0 ? "-" : numberFormat.format(bill.getFee());
        }
    }

    @Cached(watch = "bill")
    public List<OrderEntity> getOrders() {
        return Stream.concat(getBillService().findClosedOrders(bill).stream(), getBillService().findOpenedOrders(bill).stream()).collect(Collectors.toList());
    }

    public String getBillTotalOrdersSum() {
        synchronized (numberFormat) {
            return bill.getTotalOrdersSum() == 0 ? "-" : numberFormat.format(bill.getTotalOrdersSum());
        }
    }

    public String getOrderTotalPrice() {
        synchronized (numberFormat) {
            return order.getTotalPrice() == null ? "-" : numberFormat.format(order.getTotalPrice());
        }
    }

    public String getOrderStatus() {
        return getMessages().get(order.getStatus().name());
    }

}
