package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.PropertyValueEntity;

import java.util.List;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class Sitemap extends AbstractPage {

    @Property
    private PropertyValueEntity category;

    @Property
    private PropertyValueEntity subCategory;

    @Cached
    public List<PropertyValueEntity> getCategories() {
        return getPropertyService().findAllValues(PropertyValueEntity.Tag.ROOT);
    }

}
