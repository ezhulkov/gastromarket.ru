package org.ohm.gastro.gui.pages.product;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.gui.mixins.ScrollableProducts;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class List extends ScrollableProducts {

    @Property
    private ProductEntity oneProduct;

    @Property
    private boolean searchMode = false;

    public boolean onActivate() {
        initScrollableContext(null, null, null, null);
        return true;
    }

    public boolean onActivate(String cid) {
        return onActivate(null, cid, null);
    }

    public boolean onActivate(String ppid, String cid) {
        return onActivate(ppid, cid, null);
    }

    public boolean onActivate(String ppid, String cid, String eid) {
        initScrollableContext(ppid, cid, eid, null);
        return true;
    }

    public Object[] onPassivate() {
        return new Object[]{
                parentPropertyValue == null ? null : parentPropertyValue.getAltId(),
                categoryPropertyValue == null ? null : categoryPropertyValue.getAltId(),
                eventPropertyValue == null ? null : eventPropertyValue.getAltId()};
    }

    public String getTitle() {
        final PropertyValueEntity value = ObjectUtils.defaultIfNull(categoryPropertyValue, eventPropertyValue);
        return value == null ?
                getMessages().get("catalog.title") :
                (parentPropertyValue == null ? "" : parentPropertyValue.getName() + " - ") + value.getName();
    }

}
