package org.ohm.gastro.domain;

import com.google.common.collect.Lists;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Created by ezhulkov on 24.08.14.
 */
@Entity
@Table(name = "property")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PropertyEntity extends AbstractBaseEntity {

    public enum Type {
        NUMBER, TEXT, LIST, OPEN
    }

    @Column
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private Type type = Type.LIST;

    @Column
    private boolean mandatory = false;

    @Column
    private boolean hidden = false;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "property")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<PropertyValueEntity> values = Lists.newArrayList();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "property")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<TagEntity> products = Lists.newArrayList();

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public List<TagEntity> getProducts() {
        return products;
    }

    public void setProducts(final List<TagEntity> products) {
        this.products = products;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(final boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<PropertyValueEntity> getValues() {
        return values;
    }

    public void setValues(List<PropertyValueEntity> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "PropertyEntity{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", values=" + values +
                '}';
    }

}
