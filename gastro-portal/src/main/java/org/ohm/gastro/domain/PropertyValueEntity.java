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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "value_value",
            joinColumns = @JoinColumn(name = "parent_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "child_id", referencedColumnName = "id")
    )
    private List<PropertyValueEntity> children = Lists.newArrayList();

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "value_value",
            joinColumns = @JoinColumn(name = "child_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "parent_id", referencedColumnName = "id")
    )
    private List<PropertyValueEntity> parents = Lists.newArrayList();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
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

    public List<PropertyValueEntity> getChildren() {
        return children;
    }

    public void setChildren(List<PropertyValueEntity> children) {
        this.children = children;
    }

    public List<PropertyValueEntity> getParents() {
        return parents;
    }

    public void setParents(List<PropertyValueEntity> parents) {
        this.parents = parents;
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
