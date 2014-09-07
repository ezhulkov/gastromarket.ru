package org.ohm.gastro.gui.pages.admin.catalog;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CategoryEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.gui.AbstractServiceCallback;
import org.ohm.gastro.gui.ServiceCallback;
import org.ohm.gastro.gui.misc.GenericSelectModel;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.EditObjectPage;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Index extends EditObjectPage<CatalogEntity> {

    @Property
    private PropertyEntity oneProperty;

    @Property
    private ProductEntity product;

    @Property
    private ProductEntity oneProduct;

    @Property
    private TagEntity oneTag;

    @Property
    private PropertyValueEntity oneValue;

    @Property
    private CategoryEntity category;

    @Property
    private GenericSelectModel<CategoryEntity> categoryModel;

    @Component(id = "name", parameters = {"value=object?.name", "validate=maxlength=64,required"})
    private TextField nameField;

    @Component(id = "productPrice", parameters = {"value=product?.price", "validate=required"})
    private TextField pPriceField;

    @Component(id = "productName", parameters = {"value=product?.name", "validate=maxlength=64,required"})
    private TextField pNameField;

    @Component(id = "productCategory", parameters = {"model=categoryModel", "encoder=categoryModel", "value=product?.category", "validate=required"})
    private Select pCategoryField;

    @Inject
    @Property
    private Block propertyBlock;

    @Cached
    public java.util.List<ProductEntity> getProducts() {
        return getCatalogService().findAllProducts(null, getObject());
    }

    @Override
    public void activated() {
        category = getCatalogService().findAllCategories().get(0);
        product = new ProductEntity();
        categoryModel = new GenericSelectModel<>(getCatalogService().findAllCategories(), CategoryEntity.class, "name", "id", getPropertyAccess());
    }

    @Override
    public ServiceCallback<CatalogEntity> getServiceCallback() {
        return new AbstractServiceCallback<CatalogEntity>() {

            @Override
            public CatalogEntity findObject(String id) {
                return getCatalogService().findCatalog(Long.parseLong(id));
            }

            @Override
            public Class<? extends BaseComponent> deleteObject(CatalogEntity object) {
                getCatalogService().deleteCatalog(object.getId());
                return List.class;
            }

            @Override
            public Class<? extends BaseComponent> updateObject(CatalogEntity object) {
                getCatalogService().saveCatalog(object);
                return Index.class;
            }
        };
    }

    public void onSubmitFromAddProductForm() {
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
        product.setCatalog(getObject());
        getCatalogService().saveProduct(product, propValues, listValues);
    }

    public void onActionFromDeleteProduct(Long id) {
        getCatalogService().deleteProduct(id);
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
        Collections.sort(allProperties, new Comparator<PropertyEntity>() {
            @Override
            public int compare(PropertyEntity o1, PropertyEntity o2) {
                return o1.getType().compareTo(o2.getType());
            }
        });
        return allProperties;
    }

    public String getValueType() {
        return oneProperty.getType().toString().toLowerCase();
    }

    public java.util.List<TagEntity> getProductTags() {
        return getProductTags(oneProduct);
    }

}
