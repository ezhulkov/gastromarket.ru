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
@Table(name = "property_value")
public class PropertyValueEntity extends AbstractBaseEntity {

    public enum Tag {
        ROOT
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "value")
    @SequenceGenerator(initialValue = 1, allocationSize = 1, name = "value")
    private Long id;

    @Column
    private String value;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private PropertyValueEntity parent;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "parent")
    private List<PropertyValueEntity> children = Lists.newArrayList();

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private PropertyEntity property;

    @Column
    @Enumerated(EnumType.STRING)
    private Tag tag;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PropertyValueEntity getParent() {
        return parent;
    }

    public void setParent(PropertyValueEntity parent) {
        this.parent = parent;
    }

    public List<PropertyValueEntity> getChildren() {
        return children;
    }

    public void setChildren(List<PropertyValueEntity> children) {
        this.children = children;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public PropertyEntity getProperty() {
        return property;
    }

    public void setProperty(PropertyEntity property) {
        this.property = property;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "PropertyValueEntity{" +
                "id=" + id +
                ", value='" + value + '\'' +
                '}';
    }

}
