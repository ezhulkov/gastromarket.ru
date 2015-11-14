package org.ohm.gastro.gui.pages.office.bills;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.BillEntity;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.text.NumberFormat;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class List extends BaseComponent {

    @Property
    private BillEntity bill;

    @Property
    private OrderEntity order;

    @Property
    private CatalogEntity catalog;

    private final ThreadLocal<NumberFormat> numberFormat = new ThreadLocal<NumberFormat>() {
        @Override
        public NumberFormat get() {
            return NumberFormat.getCurrencyInstance();
        }
    };

    public boolean onActivate() {
        catalog = getAuthenticatedUser().getFirstCatalog().orElse(null);
        getBillService().createMissingBills(catalog);
        return true;
    }

    public boolean onActivate(Long catId) {
        catalog = isAdmin() ? getCatalogService().findCatalog(catId) : getAuthenticatedUser().getFirstCatalog().orElse(null);
        return true;
    }

    @Cached
    public java.util.List<BillEntity> getBills() {
        return getBillService().findAllBills(catalog).stream().sorted(((o1, o2) -> o2.getDate().compareTo(o1.getDate()))).collect(Collectors.toList());
    }

    public String getBillStatus() {
        return getMessages().get("BILL." + bill.getStatus().name());
    }

    public String getBillFee() {
        return bill.getFee() == 0 ? "-" : numberFormat.get().format(bill.getFee());
    }

    @Cached(watch = "bill")
    public java.util.List<OrderEntity> getOrders() {
        return Stream.concat(getBillService().findClosedOrders(bill).stream(), getBillService().findOpenedOrders(bill).stream()).collect(Collectors.toList());
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

}
