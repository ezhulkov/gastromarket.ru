package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.PropertyValueEntity.Tag;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.ProductService.OrderType;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;

/**
 * Created by ezhulkov on 14.02.15.
 */
public class Filter extends BaseComponent {

    @Property
    private PropertyValueEntity oneValue;

    @Property
    private PropertyValueEntity oneChildValue;

    @Parameter(name = "value", allowNull = true, required = false)
    private PropertyValueEntity value;

    @Parameter(name = "parentValue", allowNull = true, required = false)
    private PropertyValueEntity parentValue;

    @Parameter(name = "orderType", allowNull = true, required = false)
    private OrderType orderType;

    @Property
    @Parameter(name = "direction", allowNull = true, required = false)
    private Direction direction;

    @Property
    @Parameter(name = "pageContext", allowNull = false, required = false)
    private String pageContext;

    @Cached
    public java.util.List<PropertyValueEntity> getValues() {
        return getPropertyService().findAllValues(Tag.ROOT);
    }

    public String getPropertyName() {
        return value == null ? getMessages().get("property.select") : value.getName().toLowerCase();
    }

    public String getPropertyId() {
        return value == null ? "$N" : value.getAltId();
    }

    public String getParentPropertyId() {
        return parentValue == null ? "$N" : parentValue.getAltId();
    }

    public String getOrderMessage() {
        return orderType == null ? getMessages().get(OrderType.NONE.name()) : getMessages().get(orderType.name());
    }

    public List<PropertyValueEntity> getChildrenValues() {
        return getPropertyService().findAllChildrenValues(oneValue);
    }

}
