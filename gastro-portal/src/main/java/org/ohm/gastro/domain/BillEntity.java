package org.ohm.gastro.domain;

import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.ohm.gastro.util.CommonsUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by ezhulkov on 24.08.14.
 */
@Entity
@Table(name = "bill")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BillEntity extends AbstractBaseEntity {

    public enum Status {
        EMPTY, PAID, UNPAID, FREE
    }

    @Column(name = "bill_number")
    private int billNumber;

    @Column(name = "date")
    private Date date;

    @Column(name = "total_orders_sum")
    private int totalOrdersSum;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private CatalogEntity catalog;

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public DateTime getDateAsJoda() {
        return LocalDateTime.fromDateFields(date).toDateTime();
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public int getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(final int billNumber) {
        this.billNumber = billNumber;
    }

    public CatalogEntity getCatalog() {
        return catalog;
    }

    public void setCatalog(final CatalogEntity catalog) {
        this.catalog = catalog;
    }

    public boolean isCurrent() {
        return false;
    }

    public int getTotalOrdersSum() {
        return totalOrdersSum;
    }

    public void setTotalOrdersSum(int totalOrdersSum) {
        this.totalOrdersSum = totalOrdersSum;
    }

    public Date getClosingDate() {
        return getClosingDateAsJoda().toDate();
    }

    public DateTime getClosingDateAsJoda() {
        return getDateAsJoda().plusMonths(1);
    }

    public String getDatePrintable() {
        return CommonsUtils.GUI_DATE.get().format(date);
    }

    public String getClosingDatePrintable() {
        return CommonsUtils.GUI_DATE.get().format(getClosingDateAsJoda().minusDays(1).toDate());
    }

    public int getFee() {
        return status == Status.FREE ? 0 : (int) Math.ceil((float) ObjectUtils.defaultIfNull(totalOrdersSum, 0) / 10);
    }

    @Override
    public String toString() {
        return "BillEntity{" +
                "billNumber=" + billNumber +
                ", date=" + date +
                ", status=" + status +
                '}';
    }

}
