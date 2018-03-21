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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Created by ezhulkov on 24.08.14.
 */
@Entity
@Table(name = "property_value")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PropertyValueEntity extends AltIdBaseEntity {

    public enum Tag {
        ROOT, EVENT
    }

    @Column(name = "root_value")
    private Boolean rootValue = true;

    @Column(name = "client_generated")
    private Boolean clientGenerated = false;

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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, mappedBy = "value")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<TagEntity> tags = Lists.newArrayList();

    @Column
    @Enumerated(EnumType.STRING)
    private Tag tag;

    @Column(name = "main_page")
    private boolean mainPage = false;

    public boolean isMainPage() {
        return mainPage;
    }

    public void setMainPage(final boolean mainPage) {
        this.mainPage = mainPage;
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

    public Boolean getClientGenerated() {
        return clientGenerated;
    }

    public void setClientGenerated(Boolean clientGenerated) {
        this.clientGenerated = clientGenerated;
    }

    public List<TagEntity> getTags() {
        return tags;
    }

    public void setTags(List<TagEntity> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "PropertyValueEntity{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                '}';
    }

}
