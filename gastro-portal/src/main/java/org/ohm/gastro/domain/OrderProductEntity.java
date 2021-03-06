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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "order_product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class OrderProductEntity extends AbstractBaseEntity {

    @Column
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private PriceModifierEntity modifier;

    @Any(metaColumn = @Column(name = "entity_type"), fetch = FetchType.LAZY)
    @AnyMetaDef(idType = "long", metaType = "string",
            metaValues = {
                    @MetaValue(targetEntity = ProductEntity.class, value = "PRODUCT"),
                    @MetaValue(targetEntity = OfferEntity.class, value = "OFFER"),
            })
    @JoinColumn(name = "entity_id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private PurchaseEntity entity;

    @Column
    private int count = 1;

    public OrderProductEntity() {
    }

    public OrderProductEntity(Long id) {
        setId(id);
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public PurchaseEntity getEntity() {
        return entity;
    }

    public void setEntity(final PurchaseEntity entity) {
        this.entity = entity;
    }

    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    public PriceModifierEntity getModifier() {
        return modifier;
    }

    public void setModifier(final PriceModifierEntity modifier) {
        this.modifier = modifier;
    }

    public boolean equals(PurchaseEntity.Type type, Long id, Long mid) {
        return Objects.equals(mid, modifier == null ? null : modifier.getId()) &&
                entity.getType() == type &&
                entity.getId().equals(id);
    }

}
