package org.ohm.gastro.domain;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.MetaValue;
import org.joda.time.DateTime;
import org.ohm.gastro.util.CommonsUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Entity
@Table(name = "comment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CommentEntity extends AbstractBaseEntity {

    @Column
    private String text;

    @Column
    private Integer budget;

    @Column(name = "delivery_budget")
    private Integer deliveryBudget;

    @Column(name = "reply_expiration_hours")
    private Integer replyExpirationHours;

    @Column(name = "need_moderation")
    private Boolean needModeration;

    @Column
    private Integer rating;

    @Column
    private Date date = new Date();

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private UserEntity author;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, mappedBy = "comment")
    private List<PhotoEntity> photos = Lists.newArrayList();

    @Any(metaColumn = @Column(name = "entity_type"), fetch = FetchType.LAZY)
    @AnyMetaDef(idType = "long", metaType = "string",
            metaValues = {
                    @MetaValue(targetEntity = UserEntity.class, value = "USER"),
                    @MetaValue(targetEntity = OrderEntity.class, value = "ORDER"),
                    @MetaValue(targetEntity = CatalogEntity.class, value = "CATALOG"),
                    @MetaValue(targetEntity = ConversationEntity.class, value = "CONVERSATION"),
            })
    @JoinColumn(name = "entity_id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private CommentableEntity entity;

    public Boolean getNeedModeration() {
        return needModeration;
    }

    public void setNeedModeration(final Boolean needModeration) {
        this.needModeration = needModeration;
    }

    public List<PhotoEntity> getPhotos() {
        return photos;
    }

    public void setPhotos(final List<PhotoEntity> photos) {
        this.photos = photos;
    }

    public String getText() {
        return StringEscapeUtils.unescapeHtml4(text);
    }

    public void setText(final String text) {
        this.text = StringEscapeUtils.unescapeHtml4(text);
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(final Integer rating) {
        this.rating = rating;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public void setAuthor(final UserEntity author) {
        this.author = author;
    }

    public Integer getDeliveryBudget() {
        return deliveryBudget;
    }

    public void setDeliveryBudget(final Integer deliveryBudget) {
        this.deliveryBudget = deliveryBudget;
    }

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(final Integer budget) {
        this.budget = budget;
    }

    public CommentableEntity getEntity() {
        return entity;
    }

    public void setEntity(final CommentableEntity entity) {
        this.entity = entity;
    }

    public String getTextRaw() {
        String text = ObjectUtils.defaultIfNull(this.text, "");
        text = text.replaceAll("\\n", "<br/>");
        return text;
    }

    public Boolean getOpinion() {
        return rating == null || rating >= 0;
    }

    public void setOpinion(Boolean opinion) {
        if (opinion == null) rating = 0;
        else rating = opinion ? 1 : -1;
    }

    public String getDatePrintable() {
        return CommonsUtils.GUI_DATE_LONG.get().format(date);
    }

    public Integer getReplyExpirationHours() {
        return replyExpirationHours;
    }

    public void setReplyExpirationHours(final Integer replyExpirationHours) {
        this.replyExpirationHours = replyExpirationHours;
    }

    public long getReplyTimeLeft() {
        return replyExpirationHours == null ? Long.MAX_VALUE : new DateTime(date).plusHours(replyExpirationHours).getMillis() - System.currentTimeMillis();
    }

    public String getReplyTimeLeftPrintable() {
        final long timeLeft = getReplyTimeLeft();
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(timeLeft),
                             TimeUnit.MILLISECONDS.toMinutes(timeLeft) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeLeft)),
                             TimeUnit.MILLISECONDS.toSeconds(timeLeft) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeLeft)));
    }

    public boolean isReplyExpired() {
        return entity instanceof OrderEntity && !((OrderEntity) entity).isOrderAttached() && replyExpirationHours != null && getReplyTimeLeft() < 0;
    }

}