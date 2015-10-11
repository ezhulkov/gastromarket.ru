package org.ohm.gastro.domain;

import com.google.common.collect.Lists;
import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.MetaValue;
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

@Entity
@Table(name = "comment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CommentEntity extends AbstractBaseEntity {

    @Column
    private String text;

    @Column
    private Integer rating;

    @Column
    private Date date = new Date();

    @Column(name = "email_sent")
    private boolean emailSent;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private UserEntity author;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, mappedBy = "comment")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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

    public List<PhotoEntity> getPhotos() {
        return photos;
    }

    public void setPhotos(final List<PhotoEntity> photos) {
        this.photos = photos;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
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

    public CommentableEntity getEntity() {
        return entity;
    }

    public void setEntity(final CommentableEntity entity) {
        this.entity = entity;
    }

    public String getTextRaw() {
        String text = (String) ObjectUtils.defaultIfNull(this.text, "");
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

    public boolean isEmailSent() {
        return emailSent;
    }

    public void setEmailSent(final boolean emailSent) {
        this.emailSent = emailSent;
    }

    public String getDatePrintable() {
        return CommonsUtils.GUI_DATE_LONG.get().format(date);
    }

    public String getAvatarUrl() {
        return getAuthor().getFirstCatalog().map(CatalogEntity::getAvatarUrlMedium).orElse(getAuthor().getAvatarUrlMedium());
    }

    public String getLinkUrl() {
        return getAuthor().getFirstCatalog().map(CatalogEntity::getFullUrl).orElse(getAuthor().getFullUrl());
    }

    public String getLinkName() {
        return getAuthor().getFirstCatalog().map(CatalogEntity::getName).orElse(getAuthor().getFullName());
    }

}