package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Select;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.gui.misc.GenericSelectModel;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Filter extends BaseComponent {

    @Property
    @Parameter(name = "category", required = false, allowNull = true)
    private CategoryEntity category;

    @Component(id = "categories", parameters = {"model=categoryModel", "encoder=categoryModel", "value=category"})
    private Select pCategoryField;

    @Property
    private GenericSelectModel<CategoryEntity> categoryModel;

    public void beginRender() {
        categoryModel = new GenericSelectModel<>(getCatalogService().findAllCategories(), CategoryEntity.class, "name", "id", getPropertyAccess());
    }

}

