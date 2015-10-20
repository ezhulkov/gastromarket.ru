package org.ohm.gastro.domain;

import com.google.common.collect.Lists;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
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
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by ezhulkov on 24.08.14.
 */
@Entity
@Table(name = "orders")
public class OrderEntity extends SitemapBaseEntity implements CommentableEntity {

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
                new Status[]{Status.CLOSED, Status.CANCELLED}
        ),
        CONFIRMED(
                2,
                new Status[]{Status.CANCELLED},
                new Status[]{Status.PROGRESS, Status.CANCELLED}
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

    public enum Type {
        PUBLIC, PRIVATE
    }

    @Column
    @Enumerated(EnumType.STRING)
    private Type type = Type.PRIVATE;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "attach_reason")
    private String attachReason;

    @Column
    private String comment;

    @Column(name = "promo_code")
    private String promoCode;

    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "attach_time")
    private Date attachTime;

    @Column(name = "trigger_time")
    private Date triggerTime = new Date();

    @Column(name = "views_count")
    private Integer viewsCount = 0;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status = Status.NEW;

    @Column
    private Date date = new Date();

    @Column(name = "user_bonuses")
    private int usedBonuses = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity customer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    private List<OrderProductEntity> products = Lists.newArrayList();

    @ManyToOne(fetch = FetchType.LAZY)
    private CatalogEntity catalog;

    @Column(name = "total_price")
    private Integer totalPrice;

    @Column(name = "person_count")
    private Integer personCount;

    @Column(name = "was_setup")
    private boolean wasSetup = false;

    @Column(name = "annonce_sent")
    private boolean annonceSent = false;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, mappedBy = "order")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<PhotoEntity> photos = Lists.newArrayList();

    public List<PhotoEntity> getPhotos() {
        return photos;
    }

    public void setPhotos(final List<PhotoEntity> photos) {
        this.photos = photos;
    }

    public boolean isAnnonceSent() {
        return annonceSent;
    }

    public void setAnnonceSent(boolean annonceSent) {
        this.annonceSent = annonceSent;
    }

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
        return CommonsUtils.GUI_DATE.get().format(new Date(date.getTime()));
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(final Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getPersonCount() {
        return personCount;
    }

    public void setPersonCount(final Integer personCount) {
        this.personCount = personCount;
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

    public Date getDueDate() {
        return dueDate;
    }

    public DateTime getDueDateAsJoda() {
        return new DateTime(dueDate);
    }

    public void setDueDate(final Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueDateAsString() {
        return dueDate == null ? "" : CommonsUtils.GUI_DATE.get().format(dueDate);
    }

    public void setDueDateAsString(final String dueDate) {
        try {
            this.dueDate = StringUtils.isEmpty(dueDate) ? null : CommonsUtils.GUI_DATE.get().parse(dueDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getMetaStatusString() {
        return getMetaStatus().name().toLowerCase();
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
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

    public String getCommentRaw() {
        String text = (String) ObjectUtils.defaultIfNull(comment, "");
        text = text.replaceAll("\\n", "<br/>");
        return text;
    }

    public String getOrderUrl() {
        return getCatalog() == null ?
                String.format("http://gastromarket.ru/tender/%s", getId()) :
                String.format("http://gastromarket.ru/office/order/true/%s", getId());
    }

    @Override
    public String getLocationUrl() {
        return "http://gastromarket.ru/tender/" + getId();
    }

    @Transient
    public int getBonus() {
        return getBonus(getTotalPrice());
    }

    @Transient
    public int getReferrerBonus() {
        return (int) Math.ceil(getBonus() / 2);
    }

    public boolean isWasSetup() {
        return wasSetup;
    }

    public void setWasSetup(final boolean wasSetup) {
        this.wasSetup = wasSetup;
    }

    public String getAttachReason() {
        return attachReason;
    }

    public void setAttachReason(String attachReason) {
        this.attachReason = attachReason;
    }

    public Integer getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(Integer viewsCount) {
        this.viewsCount = viewsCount;
    }

    public Date getAttachTime() {
        return attachTime;
    }

    public void setAttachTime(Date attachTime) {
        this.attachTime = attachTime;
    }

    public Date getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(Date triggerTime) {
        this.triggerTime = triggerTime;
    }

    public CommentableEntity.Type getCommentableType() {
        return CommentableEntity.Type.ORDER;
    }

    //Helpers
    public final boolean isTender() {
        return type == Type.PUBLIC;
    }

    public final boolean isOrder() {
        return !isTender();
    }

    public final boolean isAccessAllowed(final UserEntity user) {
        return user != null &&
                (user.isAdmin() ||
                        getCustomer() != null && getCustomer().equals(user) ||
                        getCatalog() != null && getCatalog().getUser().equals(user));
    }

    public final boolean isContactsAllowed(final UserEntity user) {
        return user != null && user.isCook() && getStatus().getLevel() >= Status.CONFIRMED.getLevel();
    }

    public final boolean isTenderActive() {
        return isTender() && !isTenderExpired() && !isTenderAttached();
    }

    public final boolean isTenderAttached() {
        return getCatalog() != null;
    }

    public final boolean isTenderExpired() {
        return !isTenderAttached() && dueDate != null && LocalDateTime.fromDateFields(dueDate).toDateTime().withTimeAtStartOfDay().plusDays(1).isBeforeNow();
    }

    public final boolean isOrderClosed() {
        return getStatus().getLevel() >= Status.CLOSED.getLevel();
    }

    public final boolean isCanEdit(final UserEntity user) {
        return isOrderOwner(user) && getStatus().getLevel() <= Status.ACTIVE.getLevel();
    }

    public final boolean isOrderOwner(final UserEntity user) {
        return user != null && getCustomer().equals(user);
    }

    public static int getBonus(int total) {
        return (int) Math.ceil(total * 3 / 100);
    }

}
