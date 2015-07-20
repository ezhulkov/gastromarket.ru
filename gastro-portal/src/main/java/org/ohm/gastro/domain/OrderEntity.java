package org.ohm.gastro.domain;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.ohm.gastro.util.CommonsUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by ezhulkov on 24.08.14.
 */
@Entity
@Table(name = "orders")
public class OrderEntity extends AbstractBaseEntity {

    public enum Status {
        NEW, ACCEPTED, READY, CANCELLED
    }

    @Column(name = "order_number")
    private String orderNumber;

    @Column
    private String comment;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status = Status.NEW;

    @Column
    private Timestamp date = new Timestamp(System.currentTimeMillis());

    @Column(name = "user_bonuses")
    private int usedBonuses = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private BillEntity bill;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    private List<OrderProductEntity> products = Lists.newArrayList();

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public UserEntity getCustomer() {
        return customer;
    }

    public void setCustomer(UserEntity customer) {
        this.customer = customer;
    }

    public List<OrderProductEntity> getProducts() {
        return products;
    }

    public void setProducts(final List<OrderProductEntity> products) {
        this.products = products;
    }

    public int getUsedBonuses() {
        return usedBonuses;
    }

    public void setUsedBonuses(int usedBonuses) {
        this.usedBonuses = usedBonuses;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(final String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getDatePrintable() {
        return CommonsUtils.GUI_DATE_LONG.get().format(new Date(date.getTime()));
    }

    public BillEntity getBill() {
        return bill;
    }

    public void setBill(BillEntity bill) {
        this.bill = bill;
    }

    public boolean isClosed() {
        return status == Status.READY;
    }

    public int getOrderTotalPrice() {
        return products.stream().mapToInt(t -> t.getCount() * t.getPrice()).sum();
    }

    public CatalogEntity getCatalog() {
        return CollectionUtils.isEmpty(products) ? null : products.get(0).getProduct().getCatalog();
    }

}
