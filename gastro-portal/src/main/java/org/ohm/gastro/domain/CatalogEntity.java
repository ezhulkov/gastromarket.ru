package org.ohm.gastro.domain;

import com.google.common.collect.Lists;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

/**
 * Created by ezhulkov on 27.08.14.
 */
@Entity
@Table(name = "catalog")
public class CatalogEntity extends AbstractBaseEntity implements AltIdEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "catalog")
    @SequenceGenerator(initialValue = 1, allocationSize = 1, name = "catalog")
    private Long id;

    @Column(name = "alt_id")
    private String altId;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String delivery;

    @Column
    private String payment;

    @Column
    private Integer rating = 0;

    @Column(name = "basket_min")
    private Integer basketMin = 0;

    @Column(name = "was_setup")
    private boolean wasSetup = false;

    @Column
    private Date date = new Date(System.currentTimeMillis());

    @Column(name = "avatar_url")
    private String avatarUrl = "/img/avatar-stub.png";

    @Column(name = "avatar_url_medium")
    private String avatarUrlMedium = "/img/avatar-stub-medium.png";

    @Column(name = "avatar_url_small")
    private String avatarUrlSmall = "/img/avatar-stub-small.png";

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = ProductEntity.class, mappedBy = "catalog", orphanRemoval = true)
    private List<ProductEntity> products = Lists.newArrayList();

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<ProductEntity> getProducts() {
        return products;
    }

    public void setProducts(List<ProductEntity> products) {
        this.products = products;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
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

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public Integer getBasketMin() {
        return basketMin;
    }

    public void setBasketMin(final Integer basketMin) {
        this.basketMin = basketMin;
    }

    public boolean isWasSetup() {
        return wasSetup;
    }

    public void setWasSetup(final boolean wasSetup) {
        this.wasSetup = wasSetup;
    }

}