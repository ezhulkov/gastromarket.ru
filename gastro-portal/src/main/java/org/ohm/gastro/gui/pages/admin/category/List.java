package org.ohm.gastro.gui.pages.admin.category;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.gui.AbstractServiceCallback;
import org.ohm.gastro.gui.ServiceCallback;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.EditObjectPage;
import org.ohm.gastro.service.CatalogService;

import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class List extends EditObjectPage<CategoryEntity> {

    @Property
    private CategoryEntity oneCategory;

    @InjectService("catalogService")
    private CatalogService catalogService;

    @Component(id = "name", parameters = {"value=object?.name", "validate=maxlength=64,required"})
    private TextField nameField;

    @Cached
    public java.util.List getCategories() {
        return catalogService.findAllCategories();
    }

    @Override
    public ServiceCallback<CategoryEntity> getServiceCallback() {
        return new AbstractServiceCallback<CategoryEntity>() {

            @Override
            public Class<? extends BaseComponent> addObject(CategoryEntity category) {
                catalogService.saveCategory(category);
                return List.class;
            }

            @Override
            public CategoryEntity newObject() {
                return new CategoryEntity();
            }

        };
    }

    public void onActionFromDelete(Long id) {
        catalogService.deleteCategory(id);
    }

    public String getCategoryProperties() {
        java.util.List<PropertyEntity> properties = getCatalogService().findAllProperties(oneCategory);
        return properties.stream().map(PropertyEntity::getName).collect(Collectors.joining(","));
    }

}
