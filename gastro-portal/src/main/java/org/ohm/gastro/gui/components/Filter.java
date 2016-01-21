package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.PropertyValueEntity.Tag;
import org.ohm.gastro.filter.RegionFilter;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 14.02.15.
 */
public class Filter extends BaseComponent {

    @Property
    private PropertyValueEntity oneValue;

    @Property
    private PropertyValueEntity oneChildValue;

    @Parameter(name = "categoryValue", allowNull = true, required = false)
    private PropertyValueEntity categoryValue;

    @Parameter(name = "eventValue", allowNull = true, required = false)
    private PropertyValueEntity eventValue;

    @Parameter(name = "parentValue", allowNull = true, required = false)
    private PropertyValueEntity parentValue;

    @Parameter(name = "catalog", allowNull = true, required = false)
    private CatalogEntity catalog;

    @Property
    @Parameter(name = "pageContext", allowNull = false, required = false)
    private String pageContext;

    @Cached
    public java.util.List<PropertyValueEntity> getCategoryValues() {
        return getPropertyService().findAllValues(Tag.ROOT).stream().filter(PropertyValueEntity::isMainPage).collect(Collectors.toList());
    }

    @Cached
    public java.util.List<PropertyValueEntity> getEventValues() {
        return getPropertyService().findAllValues(Tag.EVENT).stream().filter(PropertyValueEntity::isMainPage).collect(Collectors.toList());
    }

    public String getCategoryPropertyName() {
        return categoryValue == null ? getMessages().get("property.select") : categoryValue.getName().toLowerCase();
    }

    public String getEventPropertyName() {
        return eventValue == null ? getMessages().get("property.select") : eventValue.getName().toLowerCase();
    }

    public List<PropertyValueEntity> getChildrenValues() {
        return getPropertyService().findAllChildrenValues(oneValue);
    }

    public boolean isShowCategory() {
        return getProductService().findAllProductsCountCached(catalog, oneValue, catalog == null ? RegionFilter.getCurrentRegion() : null) > 0;
    }

    public boolean isShowSubCategory() {
        return getProductService().findAllProductsCountCached(catalog, oneChildValue, catalog == null ? RegionFilter.getCurrentRegion() : null) > 0;
    }

}
