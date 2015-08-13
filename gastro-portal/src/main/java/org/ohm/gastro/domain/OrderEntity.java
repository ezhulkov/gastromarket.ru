package org.ohm.gastro.domain;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
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
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by ezhulkov on 24.08.14.
 */
@Entity
@Table(name = "orders")
public class OrderEntity extends AbstractBaseEntity {

    public enum Status {
        CANCELLED(
                7,
                new Status[]{},
                new Status[]{}
        ),
        CLOSED(
                6,
                new Status[]{},
                new Status[]{}
        ),
        DONE(
                5,
                new Status[]{},
                new Status[]{Status.CLOSED}
        ),
        PROGRESS(
                4,
                new Status[]{},
                new Status[]{Status.DONE, Status.CANCELLED}
        ),
        PAID(
                3,
                new Status[]{},
                new Status[]{Status.PROGRESS, Status.CANCELLED}
        ),
        CONFIRMED(
                2,
                new Status[]{Status.CANCELLED},
                new Status[]{Status.CANCELLED}
        ),
        ACTIVE(
                1,
                new Status[]{Status.CANCELLED},
                new Status[]{Status.CONFIRMED, Status.CANCELLED}
        ),
        NEW(
                0,
                new Status[]{OrderEntity.Status.ACTIVE, OrderEntity.Status.CANCELLED},
                new Status[]{}
        );

        private final int level;
        private final Status[] clientGraph;
        private final Status[] cookGraph;

        Status(int level, Status[] clientGraph, Status[] cookGraph) {
            this.cookGraph = cookGraph;
            this.clientGraph = clientGraph;
            this.level = level;
        }

        public int getLevel() {
            return level;
        }

        public Status[] getClientGraph() {
            return clientGraph;
        }

        public Status[] getCookGraph() {
            return cookGraph;
        }

    }

    @Column(name = "order_number")
    private String orderNumber;

    @Column
    private String comment;

    @Column(name = "promo_code")
    private String promoCode;

    @Column(name = "due_date")
    private Date dueDate;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status = Status.NEW;

    @Column
    private Timestamp date = new Timestamp(System.currentTimeMillis());

    @Column(name = "user_bonuses")
    private int usedBonuses = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity customer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    private List<OrderProductEntity> products = Lists.newArrayList();

    @ManyToOne(fetch = FetchType.LAZY)
    private CatalogEntity catalog;

    public void setCatalog(final CatalogEntity catalog) {
        this.catalog = catalog;
    }

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

    public int getOrderTotalPrice() {
        return products.stream().mapToInt(t -> t.getCount() * t.getPrice()).sum();
    }

    public CatalogEntity getCatalog() {
        return catalog;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(final String promoCode) {
        this.promoCode = promoCode;
    }

    public String getDueDate() {
        return dueDate == null ? "" : CommonsUtils.GUI_DATE.get().format(dueDate);
    }

    public void setDueDate(final String dueDate) {
        try {
            this.dueDate = StringUtils.isEmpty(dueDate) ? null : CommonsUtils.GUI_DATE.get().parse(dueDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getMetaStatusString() {
        return getMetaStatus().name().toLowerCase();
    }

    public Status getMetaStatus() {
        switch (status) {
            case NEW:
                return Status.NEW;
            case CANCELLED:
            case CLOSED:
                return Status.CLOSED;
            default:
                return Status.ACTIVE;
        }
    }

}
