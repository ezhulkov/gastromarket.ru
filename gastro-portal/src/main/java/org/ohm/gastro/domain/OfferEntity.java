package org.ohm.gastro.domain;

import com.google.common.collect.Lists;
import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "offer_product",
            joinColumns = @JoinColumn(name = "offer_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<ProductEntity> products = Lists.newArrayList();

    @Transient
    private String avatarUrl;

    @Transient
    private String avatarUrlSmall;

    @PostLoad
    public void loaded() {
        avatarUrlSmall = products.size() == 0 ? "/img/offer-stub-100x100.jpg" : products.get(0).getAvatarUrlSmall();
        avatarUrl = products.size() == 0 ? "/img/offer-stub-270x270.jpg" : products.get(0).getAvatarUrlMedium();
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

    public List<ProductEntity> getProducts() {
        return products;
    }

    public void setProducts(List<ProductEntity> products) {
        this.products = products;
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
        String desc = (String) ObjectUtils.defaultIfNull(description, "");
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
        return "http://gastromarket.ru/catalog/offer/" + getAltId();
    }

}