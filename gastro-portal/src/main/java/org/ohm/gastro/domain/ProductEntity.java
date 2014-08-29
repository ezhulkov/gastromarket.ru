package org.ohm.gastro.domain;

import com.google.common.collect.Lists;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "product")
public class ProductEntity implements BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "product")
    @SequenceGenerator(initialValue = 1, allocationSize = 1, name = "product")
    private Long id;

    @Column
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CatalogEntity catalog;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CategoryEntity category;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "product")
    private List<ProductValueEntity> values = Lists.newArrayList();

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CatalogEntity getCatalog() {
        return catalog;
    }

    public void setCatalog(CatalogEntity catalog) {
        this.catalog = catalog;
    }

    public List<ProductValueEntity> getValues() {
        return values;
    }

    public void setValues(List<ProductValueEntity> values) {
        this.values = values;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

}
