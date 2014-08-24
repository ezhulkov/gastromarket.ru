package org.ohm.gastro.gui.pages.admin.category;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.gui.AbstractServiceCallback;
import org.ohm.gastro.gui.ServiceCallback;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.EditObjectPage;
import org.ohm.gastro.service.CatalogService;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Index extends EditObjectPage<CategoryEntity> {

    @Property
    private CategoryEntity category;

    @InjectService("catalogService")
    private CatalogService catalogService;

    @Component(id = "name", parameters = {"value=object?.name", "validate=maxlength=64,required"})
    private TextField nameField;

    @Override
    public ServiceCallback<CategoryEntity> getServiceCallback() {
        return new AbstractServiceCallback<CategoryEntity>() {

            @Override
            public CategoryEntity findObject(String id) {
                return catalogService.findCategory(Long.parseLong(id));
            }

            @Override
            public Class<? extends BaseComponent> deleteObject(CategoryEntity object) {
                catalogService.deleteCategory(object.getId());
                return List.class;
            }

            @Override
            public Class<? extends BaseComponent> updateObject(CategoryEntity object) {
                catalogService.saveCategory(object);
                return Index.class;
            }
        };
    }

}
