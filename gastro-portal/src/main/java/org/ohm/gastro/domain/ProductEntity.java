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
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by ezhulkov on 24.08.14.
 */
@Entity
@Table(name = "product")
public class ProductEntity extends AbstractBaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "product")
    @SequenceGenerator(initialValue = 1, allocationSize = 1, name = "product")
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private Integer price;

    @Column
    private boolean hidden = false;

    @Column
    private Timestamp date = new Timestamp(System.currentTimeMillis());

    @Column
    private Boolean promoted = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CatalogEntity catalog;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CategoryEntity category;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "product")
    private List<TagEntity> values = Lists.newArrayList();

    @Column(name = "avatar_url")
    private String avatarUrl = "/img/product-stub.png";

    @Column(name = "avatar_url_medium")
    private String avatarUrlMedium = "/img/product-stub-medium.png";


    @Column(name = "avatar_url_small")
    private String avatarUrlSmall = "/img/product-stub-small.png";

    public ProductEntity() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(final String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrlMedium() {
        return avatarUrlMedium;
    }

    public void setAvatarUrlMedium(final String avatarUrlMedium) {
        this.avatarUrlMedium = avatarUrlMedium;
    }

    public String getAvatarUrlSmall() {
        return avatarUrlSmall;
    }

    public void setAvatarUrlSmall(final String avatarUrlSmall) {
        this.avatarUrlSmall = avatarUrlSmall;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public CatalogEntity getCatalog() {
        return catalog;
    }

    public void setCatalog(CatalogEntity catalog) {
        this.catalog = catalog;
    }

    public List<TagEntity> getValues() {
        return values;
    }

    public void setValues(List<TagEntity> values) {
        this.values = values;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Boolean getPromoted() {
        return promoted;
    }

    public void setPromoted(Boolean promoted) {
        this.promoted = promoted;
    }

}