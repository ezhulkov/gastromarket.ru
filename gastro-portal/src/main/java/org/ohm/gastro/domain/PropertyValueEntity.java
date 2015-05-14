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
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PropertyValueEntity extends AbstractBaseEntity implements AltIdEntity {

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

    @Column(name = "alt_id")
    private String altId;

    @Column(name = "root_value")
    private Boolean rootValue = true;

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "value_value",
            joinColumns = @JoinColumn(name = "parent_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "child_id", referencedColumnName = "id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<PropertyValueEntity> children = Lists.newArrayList();

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "value_value",
            joinColumns = @JoinColumn(name = "child_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "parent_id", referencedColumnName = "id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<PropertyValueEntity> parents = Lists.newArrayList();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private PropertyEntity property;

    @Column
    @Enumerated(EnumType.STRING)
    private Tag tag;

    @Override
    public String getName() {
        return value;
    }

    @Override
    public String getAltId() {
        return altId == null ? id.toString() : altId;
    }

    @Override
    public void setAltId(String altId) {
        this.altId = altId;
    }

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

    public Boolean isRootValue() {
        return rootValue;
    }

    public void setRootValue(Boolean rootValue) {
        this.rootValue = rootValue;
    }

    @Override
    public String toString() {
        return "PropertyValueEntity{" +
                "id=" + id +
                ", value='" + value + '\'' +
                '}';
    }

}
