package org.ohm.gastro.gui.pages.admin.bills;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.joda.time.DateTime;
import org.ohm.gastro.domain.BillEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.util.CommonsUtils;

import java.util.Date;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class List extends BaseComponent {

    @Property
    private Date onePeriod;

    @Property
    private BillEntity oneBill;

    public java.util.List<Date> getPeriods() {
        return getBillService().findPeriods();
    }

    @Cached(watch = "onePeriod")
    public java.util.List<BillEntity> getBills() {
        return getBillService().findAllBills(onePeriod).stream().filter(t -> t.getFee() > 0).sorted(((o1, o2) -> o1.getId().compareTo(o2.getId()))).collect(Collectors.toList());
    }

    public String getTimePrintable() {
        return CommonsUtils.GUI_DATE.get().format(onePeriod) + " &mdash; " + CommonsUtils.GUI_DATE.get().format(new DateTime(onePeriod).dayOfMonth().withMaximumValue().toDate());
    }

    public String getStatus() {
        return getMessages().get(oneBill.getStatus().name());
    }

    public int getTotal() {
        return getBills().stream().mapToInt(BillEntity::getFee).sum();
    }

}
