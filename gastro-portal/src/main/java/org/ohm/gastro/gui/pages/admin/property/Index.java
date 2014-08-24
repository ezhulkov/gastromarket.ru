package org.ohm.gastro.gui.pages.admin.property;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.CatalogService;

import java.util.List;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Index extends BaseComponent {

    @Property
    private PropertyEntity property;

    @InjectService("catalogService")
    private CatalogService catalogService;

    @Cached
    public List<PropertyEntity> getProperties() {
        return catalogService.findAllProperties();
    }

}
