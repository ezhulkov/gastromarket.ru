package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Select;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.gui.misc.GenericSelectModel;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.List;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Filter extends BaseComponent {

    @Parameter(name = "category", required = false, allowNull = true)
    private CategoryEntity category;

    @Parameter(name = "catalog", required = false, allowNull = true)
    private CatalogEntity catalog;

    @Property
    @Parameter(name = "productsBlock", required = true, allowNull = false)
    private Block productsBlock;

    @Component(id = "categories", parameters = {"model=categoryModel", "encoder=categoryModel", "value=category"})
    private Select pCategoryField;

    @Property
    private GenericSelectModel<CategoryEntity> categoryModel;

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    public CatalogEntity getCatalog() {
        return catalog;
    }

    public void setCatalog(CatalogEntity catalog) {
        this.catalog = catalog;
    }

    public Block onValueChangedFromCategories(CategoryEntity category) {
        this.category = category;
        return productsBlock;
    }

    public void activate(CatalogEntity catalog, CategoryEntity category) {
        this.catalog = catalog;
        this.category = category;
        List<CategoryEntity> allCategories;
        if (catalog == null) allCategories = getCatalogService().findAllCategories();
        else allCategories = getCatalogService().findAllCategories(catalog);
        categoryModel = new GenericSelectModel<>(allCategories, CategoryEntity.class, "name", "id", getPropertyAccess());
    }

}

