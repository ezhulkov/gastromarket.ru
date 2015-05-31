package org.ohm.gastro.domain;

import com.google.common.collect.Lists;
import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by ezhulkov on 24.08.14.
 */
@Entity
@Table(name = "product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ProductEntity extends AbstractBaseEntity implements AltIdEntity {

    public enum Unit {
        PIECE, LITRE, GRAM
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "alt_id")
    private String altId;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private Integer price;

    @Column
    @Enumerated(EnumType.STRING)
    private Unit unit = Unit.PIECE;

    @Column(name = "unit_value")
    private int unitValue = 1;

    @Column
    private Timestamp date = new Timestamp(System.currentTimeMillis());

    @Column
    private Boolean promoted = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private CatalogEntity catalog;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "product")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<TagEntity> values = Lists.newArrayList();

    @Column(name = "was_setup")
    private boolean wasSetup = false;

    @Column(name = "avatar_url")
    private String avatarUrl = "/img/product-stub.png";

    @Column(name = "avatar_url_medium")
    private String avatarUrlMedium = "/img/product-stub-medium.png";

    @Column(name = "avatar_url_small")
    private String avatarUrlSmall = "/img/product-stub-small.png";

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "offer_product",
            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "offer_id", referencedColumnName = "id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<OfferEntity> offers = Lists.newArrayList();

    public ProductEntity() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getAltId() {
        return altId == null ? id.toString() : altId;
    }

    @Override
    public void setAltId(final String altId) {
        this.altId = altId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(final String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrlMedium() {
        return avatarUrlMedium;
    }

    public void setAvatarUrlMedium(final String avatarUrlMedium) {
        this.avatarUrlMedium = avatarUrlMedium;
    }

    public String getAvatarUrlSmall() {
        return avatarUrlSmall;
    }

    public void setAvatarUrlSmall(final String avatarUrlSmall) {
        this.avatarUrlSmall = avatarUrlSmall;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public CatalogEntity getCatalog() {
        return catalog;
    }

    public void setCatalog(CatalogEntity catalog) {
        this.catalog = catalog;
    }

    public List<TagEntity> getValues() {
        return values;
    }

    public void setValues(List<TagEntity> values) {
        this.values = values;
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

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(final Unit unit) {
        this.unit = unit;
    }

    public int getUnitValue() {
        return unitValue;
    }

    public Boolean isPromoted() {
        return promoted;
    }

    public void setUnitValue(int unitValue) {
        this.unitValue = unitValue;
    }

    public boolean isWasSetup() {
        return wasSetup;
    }

    public void setWasSetup(final boolean wasSetup) {
        this.wasSetup = wasSetup;
    }

    public List<OfferEntity> getOffers() {
        return offers;
    }

    public void setOffers(final List<OfferEntity> offers) {
        this.offers = offers;
    }

    public String getDescriptionRaw() {
        String desc = (String) ObjectUtils.defaultIfNull(description, "");
        desc = desc.replaceAll("\\n", "<br/>");
        return desc;
    }

}