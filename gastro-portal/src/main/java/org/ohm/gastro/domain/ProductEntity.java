package org.ohm.gastro.domain;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.ohm.gastro.service.ImageService.ImageSize;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 24.08.14.
 */
@Entity
@Table(name = "product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ProductEntity extends SitemapBaseEntity implements PurchaseEntity {

    public enum Unit {
        PIECE, LITRE, GRAM
    }

    @Column
    private String description;

    @Column
    private Integer price;

    @Column
    @Enumerated(EnumType.STRING)
    private Unit unit = Unit.PIECE;

    @Column(name = "unit_value")
    private int unitValue = 1;

    @Column(name = "position")
    private String position = "";

    @Column
    private Timestamp date = new Timestamp(System.currentTimeMillis());

    @Column
    private Boolean promoted = false;

    @Column(name = "catalog_page")
    private Boolean catalogPage = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private CatalogEntity catalog;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "product")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<TagEntity> values = Lists.newArrayList();

    @Column(name = "was_setup")
    private boolean wasSetup = false;

    @Column(name = "was_checked")
    private boolean wasChecked = false;

    @Column(name = "import_source_url")
    private String importSourceUrl;

    @Column(name = "hidden")
    private Boolean hidden = false;

    @Column(name = "avatar_url_small")
    private String avatarUrlSmall = "/img/product-stub-140x101.png";

    @Column(name = "avatar_url_medium")
    private String avatarUrlMedium = "/img/product-stub-374x270.png";

    @Column(name = "avatar_url")
    private String avatarUrl = "/img/product-stub-560x404.png";

    @Column(name = "avatar_url_big")
    private String avatarUrlBig = "/img/product-stub-1000x720.png";

    public ProductEntity() {
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @Override
    public Type getType() {
        return Type.PRODUCT;
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

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(final Unit unit) {
        this.unit = unit;
    }

    public int getUnitValue() {
        return unitValue;
    }

    public Boolean isPromoted() {
        return promoted;
    }

    public void setUnitValue(int unitValue) {
        this.unitValue = unitValue;
    }

    public boolean isWasSetup() {
        return wasSetup;
    }

    public void setWasSetup(final boolean wasSetup) {
        this.wasSetup = wasSetup;
    }

    public boolean isWasChecked() {
        return wasChecked;
    }

    public void setWasChecked(final boolean wasChecked) {
        this.wasChecked = wasChecked;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(final String position) {
        this.position = position;
    }

    public Integer getPositionOfType(String type) {
        return type == null ? Integer.MAX_VALUE : getPositionAsTypes().getOrDefault(type, Integer.MAX_VALUE);
    }

    public void setPositionOfType(String type, Integer position) {
        final Map<String, Integer> map = getPositionAsTypes();
        map.put(type, position);
        setPositionAsTypes(map);
    }

    private Map<String, Integer> getPositionAsTypes() {
        return position == null ?
                Maps.newHashMap() :
                Arrays.stream(position.split(";"))
                        .map(t -> t.split(":"))
                        .filter(t -> StringUtils.isNotEmpty(t[0]) && StringUtils.isNotEmpty(t[1]))
                        .collect(Collectors.toMap(t -> t[0], t -> Integer.parseInt(t[1])));
    }

    private void setPositionAsTypes(Map<String, Integer> map) {
        this.position = map.entrySet().stream().map(t -> String.format("%s:%s", t.getKey(), t.getValue())).collect(Collectors.joining(";"));
    }

    public String getDescriptionRaw() {
        String desc = ObjectUtils.defaultIfNull(description, "");
        desc = desc.replaceAll("\\n", "<br/>");
        return desc;
    }

    @Override
    public String getLocationUrl() {
        return "http://gastromarket.ru/product/" + getAltId();
    }

    public String getAvatarUrlBig() {
        return avatarUrlBig;
    }

    public void setAvatarUrlBig(String avatarUrlBig) {
        this.avatarUrlBig = avatarUrlBig;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public String getImportSourceUrl() {
        return importSourceUrl;
    }

    public void setImportSourceUrl(final String importSourceUrl) {
        this.importSourceUrl = importSourceUrl;
    }

    public Boolean getCatalogPage() {
        return catalogPage;
    }

    public void setCatalogPage(final Boolean catalogPage) {
        this.catalogPage = catalogPage;
    }

    public Map<ImageSize, String> getImagesSet() {
        final Map<ImageSize, String> map = Maps.newHashMap();
        map.put(ImageSize.SIZE1, getAvatarUrlSmall());
        map.put(ImageSize.SIZE2, getAvatarUrlMedium());
        map.put(ImageSize.SIZE3, getAvatarUrl());
        map.put(ImageSize.SIZE4, getAvatarUrlBig());
        return map;
    }

}