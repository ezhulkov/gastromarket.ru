package org.ohm.gastro.gui.pages.product;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.gui.mixins.ScrollableProducts;
import org.ohm.gastro.service.ProductService;
import org.ohm.gastro.service.ProductService.OrderType;
import org.springframework.data.domain.Sort.Direction;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class List extends ScrollableProducts {

    @Property
    private ProductEntity oneProduct;

    @Property
    private boolean searchMode = false;

    public boolean onActivate() {
        initScrollableContext(null, null, null, null, null);
        return true;
    }

    public boolean onActivate(String pid) {
        return onActivate(null, pid, OrderType.NONE, null);
    }

    public boolean onActivate(String ppid, String pid) {
        return onActivate(ppid, pid, OrderType.NONE, null);
    }

    public boolean onActivate(String ppid, String pid, ProductService.OrderType orderType, Direction direction) {
        initScrollableContext(ppid, pid, null, orderType, direction);
        return true;
    }

    public Object[] onPassivate() {
        return new Object[]{
                parentPropertyValue == null ? null : parentPropertyValue.getAltId(),
                propertyValue == null ? null : propertyValue.getAltId(),
                orderType == null ? null : orderType.name().toLowerCase(),
                direction == null ? null : direction.name().toLowerCase()};
    }

    public String getTitle() {
        return propertyValue == null ?
                getMessages().get("catalog.title") :
                (parentPropertyValue == null ? "" : parentPropertyValue.getValue() + " - ") + propertyValue.getValue();
    }

}
