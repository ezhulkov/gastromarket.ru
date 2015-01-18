package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.gui.misc.CategorySelectModel;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class ProductEdit extends BaseComponent {

    @Property
    private boolean error = false;

    @Property
    private PropertyEntity oneProperty;

    @Property
    private PropertyValueEntity oneValue;

    @Inject
    @Property
    private Block propertyBlock;

    @Inject
    @Property
    private Block productBlock;

    @Parameter(name = "catalog", required = true, allowNull = false)
    private CatalogEntity catalog;

    @Property
    private CategorySelectModel categoryModel;

    @Property
    private ProductEntity product;

    @Property
    @Persist
    private CategoryEntity category;

    @Component(id = "productPrice", parameters = {"value=product.price", "validate=required"})
    private TextField pPriceField;

    @Component(id = "productName", parameters = {"value=product.name", "validate=maxlength=64,required"})
    private TextField pNameField;

    @Component(id = "productDescription", parameters = {"value=product.description", "validate=maxlength=1024"})
    private TextArea descField;

    @Component(id = "productCategory", parameters = {"model=categoryModel", "encoder=categoryModel", "value=product.category", "validate=required"})
    private Select pCategoryField;

    public void activate() {
        final List<CategoryEntity> allCategories = getCatalogService().findAllRootCategories();
        categoryModel = new CategorySelectModel(allCategories, getPropertyAccess());
        if (category == null && allCategories.size() > 0) {
            final CategoryEntity firstCategory = allCategories.get(0);
            category = firstCategory.getChildren().size() == 0 ? firstCategory : firstCategory.getChildren().get(0);
        }
        product = new ProductEntity();
        product.setCategory(category);
    }

    public void onFailureFromAddProductForm() {
        error = true;
    }

    public Block onSubmitFromAddProductForm() {
        if (!error) {
            Map<Long, String> propValues = getRequest().getParameterNames().stream()
                    .filter(t -> t.startsWith("prop-"))
                    .map(t -> t.substring("prop-".length(), t.length()))
                    .collect(Collectors.toMap(Long::parseLong,
                                              key -> getRequest().getParameter("prop-" + key)
                    ));
            Map<Long, String[]> listValues = getRequest().getParameterNames().stream()
                    .filter(t -> t.startsWith("list-"))
                    .map(t -> t.substring("list-".length(), t.length()))
                    .collect(Collectors.toMap(Long::parseLong,
                                              key -> getRequest().getParameters("list-" + key)
                    ));
            product.setCatalog(catalog);
            getCatalogService().saveProduct(product, propValues, listValues);
        }
        return productBlock;
    }

    public Block onValueChangedFromProductCategory(CategoryEntity category) {
        this.category = category;
        return propertyBlock;
    }

    public java.util.List<PropertyValueEntity> getPropertyValues() {
        return getCatalogService().findAllValues(oneProperty);
    }

    public java.util.List<PropertyEntity> getCategoryProperties() {
        java.util.List<PropertyEntity> allProperties = getCatalogService().findAllProperties(category);
        Collections.sort(allProperties, (o1, o2) -> o1.getType().compareTo(o2.getType()));
        return allProperties;
    }

    public String getValueType() {
        return oneProperty.getType().toString().toLowerCase();
    }

}

