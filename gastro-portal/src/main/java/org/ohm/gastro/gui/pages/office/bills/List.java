package org.ohm.gastro.gui.pages.office.bills;

import com.google.common.collect.Lists;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.BillEntity;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.dto.Breadcrumb;
import org.ohm.gastro.gui.pages.AbstractPage;
import org.ohm.gastro.util.CommonsUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class List extends AbstractPage {

    @Property
    private BillEntity bill;

    @Property
    private OrderEntity order;

    @Property
    private CatalogEntity catalog;

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
        return bill.getFee() == 0 ? "-" : CommonsUtils.GUI_CURRENCY.get().format(bill.getFee());
    }

    @Cached(watch = "bill")
    public java.util.List<OrderEntity> getOrders() {
        return Stream.concat(getBillService().findClosedOrders(bill).stream(), getBillService().findOpenedOrders(bill).stream()).collect(Collectors.toList());
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

    @Override
    public java.util.List<Breadcrumb> getBreadcrumbsContext() {
        return Lists.newArrayList(
                mainPage,
                Breadcrumb.of(catalog.getName(), org.ohm.gastro.gui.pages.catalog.Index.class, catalog.getAltId()),
                Breadcrumb.of(getTitle(), List.class, catalog.getId())
        );
    }

}
