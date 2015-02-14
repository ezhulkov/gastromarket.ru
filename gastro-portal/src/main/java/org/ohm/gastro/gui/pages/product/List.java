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
    private String searchString = "";

    @Property
    private ProductEntity oneProduct;

    @Property
    private boolean searchMode = false;

    public boolean onActivate() {
        initScrollableContext(null, null, null, null);
        return true;
    }

    public boolean onActivate(Long cid) {
        return onActivate(cid, OrderType.NONE, null);
    }

    public boolean onActivate(String token, String searchString) {
        this.searchString = searchString;
        this.searchMode = true;
        return true;
    }

    public boolean onActivate(Long cid, ProductService.OrderType orderType, Direction direction) {
        initScrollableContext(cid, null, orderType, direction);
        return true;
    }

    public Object[] onPassivate() {
        if (searchString != null) return new Object[]{"search", searchString};
        return new Object[]{
                category == null ? null : category.getId(),
                orderType == null ? null : orderType.name().toLowerCase(),
                direction == null ? null : direction.name().toLowerCase()};
    }

    public String getTitle() {
        return category == null ? getMessages().get("catalog.title") : category.getParent() == null ? category.getName() : category.getParent().getName() + " - " + category.getName();
    }

}
