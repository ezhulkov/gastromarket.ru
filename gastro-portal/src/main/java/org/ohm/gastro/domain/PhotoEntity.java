package org.ohm.gastro.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by ezhulkov on 27.08.14.
 */
@Entity
@Table(name = "photo")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PhotoEntity extends AbstractBaseEntity {

    public enum Type {
        ORDER, COMMENT, OFFER
    }

    @Column
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column
    private String text;

    @Column(name = "avatar_url_small")
    private String avatarUrlSmall = "/img/product-stub-140x101.png";

    @Column(name = "avatar_url")
    private String avatarUrl = "/img/product-stub-374x270.png";

    @Column(name = "avatar_url_big")
    private String avatarUrlBig = "/img/product-stub-1000x720.png";

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private OfferEntity offer;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private CommentEntity comment;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private ProductEntity product;

    public PhotoEntity() {
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(final ProductEntity product) {
        this.product = product;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public CommentEntity getComment() {
        return comment;
    }

    public void setComment(CommentEntity comment) {
        this.comment = comment;
    }

    public String getAvatarUrlSmall() {
        return avatarUrlSmall;
    }

    public void setAvatarUrlSmall(final String avatarUrlSmall) {
        this.avatarUrlSmall = avatarUrlSmall;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(final String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrlBig() {
        return avatarUrlBig;
    }

    public void setAvatarUrlBig(final String avatarUrlBig) {
        this.avatarUrlBig = avatarUrlBig;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public boolean isPhotoAttached() {
        return comment != null || order != null;
    }

    public OfferEntity getOffer() {
        return offer;
    }

    public void setOffer(final OfferEntity offer) {
        this.offer = offer;
    }

    public String getLinkAvatarSmall() {
        return product == null ? avatarUrlSmall : product.getAvatarUrlSmall();
    }

    public String getLinkAvatar() {
        return product == null ? avatarUrl : product.getAvatarUrl();
    }

    public String getLinkAvatarBig() {
        return product == null ? avatarUrlBig : product.getAvatarUrlBig();
    }

}