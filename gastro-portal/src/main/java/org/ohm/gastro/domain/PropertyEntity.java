package org.ohm.gastro.domain;

import com.google.common.collect.Lists;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.List;

/**
 * Created by ezhulkov on 24.08.14.
 */
@Entity
@Table(name = "property")
public class PropertyEntity extends AbstractBaseEntity {

    public enum Type {
        NUMBER, TEXT, LIST
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "property")
    @SequenceGenerator(initialValue = 1, allocationSize = 1, name = "property")
    private Long id;

    @Column
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private Type type = Type.TEXT;

    @Column
    private boolean mandatory = false;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "property")
    private List<PropertyValueEntity> values = Lists.newArrayList();

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private PropertyEntity parent;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = PropertyEntity.class, mappedBy = "parent")
    private List<PropertyEntity> children;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "property")
    private List<TagEntity> products = Lists.newArrayList();

    @Column
    private String tag;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public PropertyEntity getParent() {
        return parent;
    }

    public void setParent(final PropertyEntity parent) {
        this.parent = parent;
    }

    public List<PropertyEntity> getChildren() {
        return children;
    }

    public void setChildren(final List<PropertyEntity> children) {
        this.children = children;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(final String tag) {
        this.tag = tag;
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
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", values=" + values +
                '}';
    }

}
