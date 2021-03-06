package org.ohm.gastro.gui.pages.office.bills;

import com.google.common.collect.Lists;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.BillEntity;
import org.ohm.gastro.domain.BillEntity.Status;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.dto.Breadcrumb;
import org.ohm.gastro.gui.pages.AbstractPage;
import org.ohm.gastro.util.CommonsUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Index extends AbstractPage {

    @Property
    private BillEntity bill;

    @Property
    private OrderEntity order;

    @Property
    private boolean test = false;

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
        return bill.getTotalOrdersSum() == 0 ? "-" : CommonsUtils.GUI_CURRENCY.get().format(bill.getTotalOrdersSum());
    }

    public String getOrderTotalPrice() {
        return order.getTotalPrice() == null ? "-" : CommonsUtils.GUI_CURRENCY.get().format(order.getTotalPrice());
    }

    public String getOrderStatus() {
        return getMessages().get(order.getStatus().name());
    }

    public boolean isUnpaid() {
        return bill.getStatus() == Status.UNPAID;
    }

    @Cached
    public String getBillFee() {
        return bill.getFee() == 0 ? "-" : CommonsUtils.GUI_CURRENCY.get().format(bill.getFee());
    }

    public String getBillStatus() {
        return getMessages().get("BILL2." + bill.getStatus().name());
    }

    @Cached(watch = "bill")
    public java.util.List<OrderEntity> getOrders() {
        return Stream.concat(getBillService().findClosedOrders(bill).stream(), getBillService().findOpenedOrders(bill).stream()).collect(Collectors.toList());
    }

    @Override
    public String getTitle() {
        return bill == null ? "" : getMessages().format("bill.title", bill.getBillNumber());
    }

    @Override
    public java.util.List<Breadcrumb> getBreadcrumbsContext() {
        return Lists.newArrayList(
                mainPage,
                Breadcrumb.of(bill.getCatalog().getName(), org.ohm.gastro.gui.pages.catalog.Index.class, bill.getCatalog().getAltId()),
                Breadcrumb.of(getMessages().get(List.class.getName()), List.class, bill.getCatalog().getId()),
                Breadcrumb.of(getTitle(), Index.class, bill.getId())
        );
    }

}
