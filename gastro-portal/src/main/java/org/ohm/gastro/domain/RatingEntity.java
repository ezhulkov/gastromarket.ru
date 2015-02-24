package org.ohm.gastro.domain;

import org.ohm.gastro.util.CommonsUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Created by ezhulkov on 27.08.14.
 */
@Entity
@Table(name = "rating")
public class RatingEntity extends AbstractBaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "rating")
    @SequenceGenerator(initialValue = 1, allocationSize = 1, name = "rating")
    private Long id;

    @Column
    private String comment;

    @Column
    private Integer rating;

    @Column
    private Timestamp date = new Timestamp(System.currentTimeMillis());

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CatalogEntity catalog;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private UserEntity author;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getDatePrintable() {
        return CommonsUtils.GUIDATE_LONG.get().format(date);
    }

}