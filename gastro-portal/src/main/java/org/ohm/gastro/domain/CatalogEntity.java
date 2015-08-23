package org.ohm.gastro.domain;

import com.google.common.collect.Lists;
import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.ohm.gastro.service.CatalogService;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 27.08.14.
 */
@Entity
@Table(name = "catalog")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CatalogEntity extends AltIdBaseEntity {

    public enum Type {
        PRIVATE, COMPANY
    }

    @Column
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column
    private String description;

    @Column
    private String cancellation;

    @Column
    private String delivery;

    @Column
    private String payment;

    @Column(name = "wizard_step")
    private Integer wizardStep = 1;

    @Column
    private Integer rating = 0;

    @Column
    private Integer level = 1;

    @Column(name = "rank_badge")
    private Integer rankBadge = 0;

    @Column(name = "product_badge")
    private Integer productBadge = 0;

    @Column(name = "order_badge")
    private Integer orderBadge = 0;

    @Column(name = "basket_min")
    private Integer basketMin = 0;

    @Column(name = "prepayment")
    private Integer prepayment;

    @Column
    private Date date = new Date(System.currentTimeMillis());

    @Column(name = "avatar_url")
    private String avatarUrl = "/img/avatar-stub-270x270.png";

    @Column(name = "avatar_url_medium")
    private String avatarUrlMedium = "/img/avatar-stub-100x100.png";

    @Column(name = "avatar_url_small")
    private String avatarUrlSmall = "/img/avatar-stub-23x23.png";

    @ManyToOne(fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private UserEntity user;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = ProductEntity.class, mappedBy = "catalog", orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<ProductEntity> products = Lists.newArrayList();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = OfferEntity.class, mappedBy = "catalog", orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<OfferEntity> offers = Lists.newArrayList();

    public String getFullUrl() {
        return "http://gastromarket.ru/catalog/" + getAltId();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
        return wizardStep != null && wizardStep >= getMaxWizardStep();
    }

    public List<ProductEntity> getReadyProducts() {
        return products.stream().filter(ProductEntity::isWasSetup).collect(Collectors.toList());
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(final Integer level) {
        this.level = level;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public Integer getWizardStep() {
        return wizardStep;
    }

    public void setWizardStep(Integer wizardStep) {
        this.wizardStep = wizardStep;
    }

    public Integer getMaxWizardStep() {
        return CatalogService.MAX_WIZARD_STEP;
    }

    public Integer getRankBadge() {
        return rankBadge;
    }

    public void setRankBadge(Integer rankBadge) {
        this.rankBadge = rankBadge;
    }

    public Integer getProductBadge() {
        return productBadge;
    }

    public void setProductBadge(Integer productBadge) {
        this.productBadge = productBadge;
    }

    public Integer getOrderBadge() {
        return orderBadge;
    }

    public void setOrderBadge(Integer orderBadge) {
        this.orderBadge = orderBadge;
    }

    public String getCancellation() {
        return cancellation;
    }

    public String getCancellationRaw() {
        return ((String) ObjectUtils.defaultIfNull(cancellation, "")).replaceAll("\\n", "<br/>");
    }

    public void setCancellation(final String cancellation) {
        this.cancellation = cancellation;
    }

    public Integer getPrepayment() {
        return prepayment;
    }

    public void setPrepayment(final Integer prepayment) {
        this.prepayment = prepayment;
    }

    public String getDescriptionRaw() {
        String desc = (String) ObjectUtils.defaultIfNull(description, "");
        desc = desc.replaceAll("\\n", "<br/>");
        return desc;
    }

    public String getDeliveryRaw() {
        String desc = (String) ObjectUtils.defaultIfNull(delivery, "");
        desc = desc.replaceAll("\\n", "<br/>");
        return desc;
    }

    public String getPaymentRaw() {
        String desc = (String) ObjectUtils.defaultIfNull(payment, "");
        desc = desc.replaceAll("\\n", "<br/>");
        return desc;
    }

    @Override
    public String toString() {
        return "CatalogEntity{" +
                "id=" + getId() +
                ", rating=" + rating +
                ", level=" + level +
                ", name='" + getName() + '\'' +
                '}';
    }

}