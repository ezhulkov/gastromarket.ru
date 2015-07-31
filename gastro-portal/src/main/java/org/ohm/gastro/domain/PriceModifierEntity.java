package org.ohm.gastro.domain;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.MetaValue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "price_modifier")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PriceModifierEntity extends AbstractBaseEntity {

    public enum Sign {PLUS, MINUS}

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Integer price;

    @Column(name = "sign")
    @Enumerated(value = EnumType.STRING)
    private Sign sign = Sign.PLUS;

    @Any(metaColumn = @Column(name = "entity_type"), fetch = FetchType.LAZY)
    @AnyMetaDef(idType = "long", metaType = "string",
            metaValues = {
                    @MetaValue(targetEntity = ProductEntity.class, value = "PRODUCT"),
                    @MetaValue(targetEntity = OfferEntity.class, value = "OFFER"),
            })
    @JoinColumn(name = "entity_id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private PriceEntity entity;

    public PriceEntity getEntity() {
        return entity;
    }

    public void setEntity(final PriceEntity entity) {
        this.entity = entity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(final Integer price) {
        this.price = price;
    }

    public Sign getSign() {
        return sign;
    }

    public String getSignPrintable() {
        return sign == Sign.MINUS ? "-" : "+";
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "PriceModifierEntity{" +
                "description='" + description + '\'' +
                ", price=" + price +
                '}';
    }

}