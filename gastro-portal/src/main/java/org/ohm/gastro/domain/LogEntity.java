package org.ohm.gastro.domain;

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
@Table(name = "logs")
public class LogEntity extends AbstractBaseEntity {

    public enum Type {
        LOGIN,
        ORDER_DONE,
        ORDER_CANCELLED,
        RATING_CHANGE
    }

    @Column
    private Date date = new Date();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private CatalogEntity catalog;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Column
    private Long count;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(final UserEntity user) {
        this.user = user;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(final Long count) {
        this.count = count;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public CatalogEntity getCatalog() {
        return catalog;
    }

    public void setCatalog(final CatalogEntity catalog) {
        this.catalog = catalog;
    }

    @Override
    public String toString() {
        return "LogEntity{" +
                "id=" + getId() +
                ", type=" + type +
                ", count=" + count +
                '}';
    }

}