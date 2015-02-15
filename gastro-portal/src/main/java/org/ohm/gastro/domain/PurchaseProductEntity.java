package org.ohm.gastro.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Created by ezhulkov on 24.08.14.
 */
@Entity
@Table(name = "purchase_product")
public class PurchaseProductEntity extends AbstractBaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "order_product")
    @SequenceGenerator(initialValue = 1, allocationSize = 1, name = "order_product")
    private Long id;

    @Column
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PurchaseEntity purchase;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ProductEntity product;

    @Column
    private int count = 1;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public PurchaseEntity getPurchase() {
        return purchase;
    }

    public void setPurchase(PurchaseEntity purchase) {
        this.purchase = purchase;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }

}
