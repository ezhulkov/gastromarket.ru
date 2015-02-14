package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.ProductService.OrderType;
import org.springframework.data.domain.Sort.Direction;

/**
 * Created by ezhulkov on 14.02.15.
 */
public class Filter extends BaseComponent {

    @Property
    private CategoryEntity oneCategory;

    @Parameter(name = "category", allowNull = true, required = true)
    private CategoryEntity category;

    @Parameter(name = "orderType", allowNull = true, required = true)
    private OrderType orderType;

    @Property
    @Parameter(name = "direction", allowNull = true, required = true)
    private Direction direction;

    @Property
    @Parameter(name = "pageContext", allowNull = false, required = true)
    private String pageContext;

    @Cached
    public java.util.List<CategoryEntity> getCategories() {
        return getCatalogService().findAllRootCategories();
    }

    public String getCategoryName() {
        return category == null ? getMessages().get("category.select") : category.getName().toLowerCase();
    }

    public String getCategoryId() {
        return category == null ? "$N" : category.getId().toString();
    }

    public String getOrderMessage() {
        return orderType == null ? getMessages().get(OrderType.NONE.name()) : getMessages().get(orderType.name());
    }

}
