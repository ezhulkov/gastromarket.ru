package org.ohm.gastro.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by ezhulkov on 24.08.14.
 */
@Entity
@Table(name = "tags")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TagEntity extends AbstractBaseEntity {

    @Column
    private String data;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private PropertyValueEntity value;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private PropertyEntity property;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private ProductEntity product;

    public TagEntity() {
    }

    public TagEntity(final String data, final PropertyEntity property) {
        this.data = data;
        this.property = property;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public PropertyEntity getProperty() {
        return property;
    }

    public void setProperty(PropertyEntity property) {
        this.property = property;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public PropertyValueEntity getValue() {
        return value;
    }

    public void setValue(PropertyValueEntity value) {
        this.value = value;
    }

}
