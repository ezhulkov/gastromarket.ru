package org.ohm.gastro.domain;

import com.google.common.collect.Lists;
import org.apache.commons.lang.time.DateUtils;
import org.ohm.gastro.util.CommonsUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 24.08.14.
 */
@Entity
@Table(name = "bill")
public class BillEntity extends AbstractBaseEntity {

    public enum Status {
        NEW, CLOSED
    }

    @Column
    @Enumerated(EnumType.STRING)
    private Status status = Status.NEW;

    @Column
    private Date date = new Date(System.currentTimeMillis());

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bill")
    private List<OrderEntity> orders = Lists.newArrayList();

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    private CatalogEntity catalog;

    @Transient
    private int totalBill;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<OrderEntity> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderEntity> orders) {
        this.orders = orders;
    }

    public CatalogEntity getCatalog() {
        return catalog;
    }

    public void setCatalog(CatalogEntity catalog) {
        this.catalog = catalog;
    }

    public List<OrderEntity> getClosedOrders() {
        return orders.stream().filter(OrderEntity::isClosed).collect(Collectors.toList());
    }

    public int getOrderCount() {
        return getClosedOrders().size();
    }

    public int getTotalSales() {
        return getClosedOrders().stream().mapToInt(OrderEntity::getOrderTotalPrice).sum();
    }

    public int getTotalBonuses() {
        return getClosedOrders().stream().mapToInt(OrderEntity::getUsedBonuses).sum();
    }

    public int getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(int totalBill) {
        this.totalBill = totalBill;
    }

    public String getDatePrintable() {
        return CommonsUtils.GUI_DATE.get().format(new Date(date.getTime()));
    }

    public boolean isCurrentMonth() {
        return DateUtils.truncate(date, Calendar.MONTH).equals(DateUtils.truncate(new Date(), Calendar.MONTH));
    }

}
