package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class FooterLayout extends BaseComponent {

    @Property
    private PropertyValueEntity category;

    @Cached
    public List<PropertyValueEntity> getCategories() {
        return getPropertyService().findAllValues(PropertyValueEntity.Tag.ROOT);
    }

}
