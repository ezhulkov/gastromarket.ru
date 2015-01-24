package org.ohm.gastro.gui.components;

import com.google.common.collect.Lists;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
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
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.gui.misc.CategorySelectModel;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class ProductEdit extends BaseComponent {

    public enum Stage {
        DESC, PROP, PHOTO, DONE
    }

    @Property
    private boolean error = false;

    @Property
    private Stage stage = Stage.DESC;

    @Property
    private boolean closeImmediately = false;

    @Property
    private PropertyEntity oneProperty;

    @Property
    private PropertyValueEntity oneValue;

    @Property
    private TagEntity oneTag;

    @Inject
    @Property
    private Block propertyBlock;

    @Inject
    @Property
    private Block productBlock;

    @Parameter(name = "catalog", required = true, allowNull = false)
    private CatalogEntity catalog;

    @Property
    @Persist
    private CategoryEntity category;

    @Component(id = "productPrice", parameters = {"value=product.price", "validate=required"})
    private TextField pPriceField;

    @Component(id = "productName", parameters = {"value=product.name", "validate=maxlength=64,required"})
    private TextField pNameField;

    @Component(id = "productDescription", parameters = {"value=product.description", "validate=maxlength=1024"})
    private TextArea descField;

    @Component(id = "productCategory", parameters = {"model=categoryModel", "encoder=categoryModel", "value=category", "validate=required"})
    private Select pCategoryField;

    @Property
    @Parameter(name = "modalId", defaultPrefix = BindingConstants.LITERAL, value = "pr-new")
    private String modalId;

    @Property
    @Parameter(defaultPrefix = BindingConstants.PROP)
    private ProductEntity product;

    @Cached
    public CategorySelectModel getCategoryModel() {
        return new CategorySelectModel(getAllCategories(), getPropertyAccess());
    }

    @Cached
    public List<CategoryEntity> getAllCategories() {
        return getCatalogService().findAllRootCategories();
    }

    public boolean isEditProduct() {
        return product != null && product.getId() != null;
    }

    public String getNextCaption() {
        if (stage == Stage.DESC) return isEditProduct() ? getMessages().get("edit.props") : getMessages().get("add.props");
        if (stage == Stage.PROP) return isEditProduct() ? getMessages().get("edit.photo") : getMessages().get("add.photo");
        return "";
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

    public String getProductTagValue() {
        return isEditProduct() ?
                getProductTags(product).stream()
                        .filter(t -> t.getProperty().equals(oneProperty))
                        .map(TagEntity::getData)
                        .findFirst().orElse("") :
                "";
    }

    public List<TagEntity> getProductListValues() {
        return isEditProduct() ?
                getProductTags(product).stream()
                        .filter(t -> t.getProperty().getType() == PropertyEntity.Type.LIST)
                        .filter(t -> t.getProperty().equals(oneProperty))
                        .flatMap(t -> Arrays.stream(t.getData().split(",")))
                        .map(t -> new TagEntity(t, oneProperty))
                        .collect(Collectors.toList()) :
                Lists.newArrayList();
    }

    private void beginRender() {
        if (product != null && product.getId() != null) {
            category = product.getCategory();
        } else {
            final CategoryEntity firstCategory = getAllCategories().get(0);
            product = new ProductEntity();
            category = firstCategory.getChildren().size() == 0 ? firstCategory : firstCategory.getChildren().get(0);
        }
    }

    public void onPrepareFromEditProductForm() {
        beginRender();
    }

    public void onFailureFromEditProductForm() {
        error = true;
    }

    public void onSelectedFromSaveAndClose() {
        closeImmediately = true;
    }

    public void onSelectedFromSaveAndNext() {
        closeImmediately = false;
    }

    public Block onSubmitFromEditProductForm(Long pid, Stage stage) {
        if (!error) {
            final ProductEntity origProduct = pid != null ? getCatalogService().findProduct(pid) : product;
            if (stage == Stage.DESC) {
                origProduct.setName(product.getName());
                origProduct.setPrice(product.getPrice());
                origProduct.setDescription(product.getDescription());
                origProduct.setCategory(category);
                origProduct.setCatalog(catalog);
                getCatalogService().saveProduct(origProduct);
                this.stage = Stage.PROP;
            } else if (stage == Stage.PROP) {
                Map<Long, String> propValues = getRequest().getParameterNames().stream()
                        .filter(t -> t.startsWith("prop-"))
                        .map(t -> t.substring("prop-".length(), t.length()))
                        .collect(Collectors.toMap(Long::parseLong, key -> getRequest().getParameter("prop-" + key)
                        ));
                Map<Long, String[]> listValues = getRequest().getParameterNames().stream()
                        .filter(t -> t.startsWith("list-"))
                        .map(t -> t.substring("list-".length(), t.length()))
                        .collect(Collectors.toMap(Long::parseLong, key -> getRequest().getParameters("list-" + key)
                        ));
                getCatalogService().saveProduct(origProduct, propValues, listValues);
                this.stage = Stage.PHOTO;
            }
            if (closeImmediately) this.stage = Stage.DONE;
        }
        return productBlock;
    }

    public Block onValueChangedFromProductCategory(CategoryEntity category) {
        this.category = category;
        return propertyBlock;
    }

}

