package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class ParamBlock extends BaseComponent {

    @Property
    private PropertyValueEntity oneValue;

    @Property
    @Parameter(name = "property", required = true, allowNull = false)
    private PropertyEntity property;

    @Property
    @Parameter(name = "tag", required = false, allowNull = false)
    private TagEntity tag;

    public java.util.List<PropertyValueEntity> getPropertyValues() {
        return getCatalogService().findAllValues(property);
    }

    public boolean isTagSelected() {
        return tag != null && tag.getData().equals(oneValue.getId().toString());
    }

}

