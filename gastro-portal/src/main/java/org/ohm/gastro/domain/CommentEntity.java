package org.ohm.gastro.domain;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
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
 * Created by ezhulkov on 27.08.14.
 */
@Entity
@Table(name = "comment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CommentEntity extends AbstractBaseEntity {

    public enum Type {
        CATALOG, CUSTOMER
    }

    @Column
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column
    private String text;

    @Column
    private Integer rating;

    @Column
    private Date date = new Date();

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private CatalogEntity catalog;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private UserEntity author;

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(final UserEntity user) {
        this.user = user;
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

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public CatalogEntity getCatalog() {
        return catalog;
    }

    public void setCatalog(CatalogEntity catalog) {
        this.catalog = catalog;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public void setAuthor(UserEntity author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDatePrintable() {
        return CommonsUtils.GUI_DATE_LONG.get().format(date);
    }

    public String getTextRaw() {
        String text = (String) ObjectUtils.defaultIfNull(this.text, "");
        text = text.replaceAll("\\n", "<br/>");
        return text;
    }

}