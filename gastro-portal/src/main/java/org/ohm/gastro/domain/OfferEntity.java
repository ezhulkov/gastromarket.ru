package org.ohm.gastro.domain;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by ezhulkov on 24.08.14.
 */
@Entity
@Table(name = "offer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class OfferEntity extends SitemapBaseEntity implements PurchaseEntity {

    @Column
    private String description;

    @Column
    private Integer price;

    @Column
    private Timestamp date = new Timestamp(System.currentTimeMillis());

    @Column
    private Boolean promoted = false;

    @Column
    private Integer persons;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private CatalogEntity catalog;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, mappedBy = "offer")
    private List<PhotoEntity> photos = Lists.newArrayList();

    @Transient
    private String avatarUrl;

    @Transient
    private String avatarUrlSmall;

    @PostLoad
    public void loaded() {
        avatarUrlSmall = photos.size() == 0 ? "/img/offer-stub-100x100.jpg" : photos.get(0).getLinkAvatarSmall();
        avatarUrl = photos.size() == 0 ? "/img/offer-stub-270x270.jpg" : photos.get(0).getLinkAvatar();
    }

    public Integer getPersons() {
        return persons;
    }

    public void setPersons(final Integer persons) {
        this.persons = persons;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @Override
    public Type getType() {
        return Type.OFFER;
    }

    public CatalogEntity getCatalog() {
        return catalog;
    }

    public void setCatalog(CatalogEntity catalog) {
        this.catalog = catalog;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Boolean getPromoted() {
        return promoted;
    }

    public void setPromoted(Boolean promoted) {
        this.promoted = promoted;
    }

    public Boolean isPromoted() {
        return promoted;
    }

    @Override
    public String getAvatarUrlSmall() {
        return avatarUrlSmall;
    }

    @Override
    public String getAvatarUrlMedium() {
        return avatarUrl;
    }

    public String getDescriptionRaw() {
        String desc = ObjectUtils.defaultIfNull(description, "");
        desc = desc.replaceAll("\\n", "<br/>");
        return desc;
    }

    @Override
    public String toString() {
        return "OfferEntity{" +
                "id='" + getId() + '\'' +
                '}';
    }

    @Override
    public String getLocationUrl() {
        return "https://gastromarket.ru/catalog/offer/" + getAltId();
    }

}