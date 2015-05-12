package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.PropertyValueEntity.Tag;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.ProductService.OrderType;
import org.springframework.data.domain.Sort.Direction;

/**
 * Created by ezhulkov on 14.02.15.
 */
public class Filter extends BaseComponent {

    @Property
    private PropertyValueEntity oneProperty;

    @Parameter(name = "property", allowNull = true, required = true)
    private PropertyEntity property;

    @Parameter(name = "orderType", allowNull = true, required = true)
    private OrderType orderType;

    @Property
    @Parameter(name = "direction", allowNull = true, required = true)
    private Direction direction;

    @Property
    @Parameter(name = "pageContext", allowNull = false, required = true)
    private String pageContext;

    @Cached
    public java.util.List<PropertyValueEntity> getProperties() {
        return getCatalogService().findAllRootValues(Tag.ROOT);
    }

    public String getPropertyName() {
        return property == null ? getMessages().get("property.select") : property.getName().toLowerCase();
    }

    public String getPropertyId() {
        return property == null ? "$N" : property.getId().toString();
    }

    public String getOrderMessage() {
        return orderType == null ? getMessages().get(OrderType.NONE.name()) : getMessages().get(orderType.name());
    }

}
