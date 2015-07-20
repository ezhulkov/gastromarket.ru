package org.ohm.gastro.domain;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.MetaValue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

/**
 * Created by ezhulkov on 24.08.14.
 */
@Entity
@Table(name = "price_modifier")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PriceModifierEntity extends AbstractBaseEntity {

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Integer price;

    @Any(metaColumn = @Column(name = "entity_type"), fetch = FetchType.LAZY)
    @AnyMetaDef(idType = "long", metaType = "string",
            metaValues = {
                    @MetaValue(targetEntity = ProductEntity.class, value = "PRODUCT"),
                    @MetaValue(targetEntity = OfferEntity.class, value = "OFFER"),
            })
    @JoinColumn(name = "entity_id")
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

    @Override
    public String toString() {
        return "PriceModifierEntity{" +
                "description='" + description + '\'' +
                ", price=" + price +
                '}';
    }

}