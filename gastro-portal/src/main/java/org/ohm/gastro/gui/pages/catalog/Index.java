package org.ohm.gastro.gui.pages.catalog;

import com.google.common.collect.Lists;
import org.apache.commons.lang.ObjectUtils;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.Collections;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Index extends BaseComponent {

    @Property
    private PropertyValueEntity oneValue;

    @Property
    private boolean edit;

    @Property
    private ProductEntity product;

    @Property
    private TagEntity oneTag;

    @Property
    private PropertyEntity oneProperty;

    @Component(id = "name", parameters = {"value=product.name", "validate=required"})
    private TextField nameField;

    @Component(id = "description", parameters = {"value=product.description"})
    private TextArea descField;

    @Component(id = "price", parameters = {"value=product.price"})
    private TextField priceField;

    public boolean onActivate(Long id) {
        return onActivate(id, "");
    }

    public boolean onActivate(Long id, String edit) {
        this.product = getCatalogService().findProduct(id);
        this.edit = "edit".equals(edit);
        return true;
    }

    public Object[] onPassivate() {
        return edit ? new Object[]{product.getId(), true} : new Object[]{product.getId()};
    }

    @Cached
    public java.util.List<TagEntity> getProductTags() {
        return getProductTags(product);
    }

    public boolean isOwner() {
        UserEntity user = getAuthenticatedUser();
        return user != null && product.getCatalog().getUser().equals(user);
    }

    public void onSubmitFromEditForm() {
        getCatalogService().saveProduct(product);
    }

    public String getValueType() {
        return oneProperty.getType().toString().toLowerCase();
    }

    public java.util.List<PropertyEntity> getCategoryProperties() {
        java.util.List<PropertyEntity> allProperties = getCatalogService().findAllProperties(product.getCategory());
        Collections.sort(allProperties, (o1, o2) -> o1.getType().compareTo(o2.getType()));
        return allProperties;
    }

    public java.util.List<PropertyValueEntity> getPropertyValues() {
        return getCatalogService().findAllValues(oneProperty);
    }

    public String getProductDescription() {
        String desc = (String) ObjectUtils.defaultIfNull(product.getDescription(), "");
        desc = desc.replaceAll("\\r\\n", "<br/>");
        return desc;
    }

    @Cached
    public java.util.List<TagEntity> getAllTags() {
        java.util.List<TagEntity> setTags = getProductTags();
        java.util.List<TagEntity> result = Lists.newArrayList(setTags);
        java.util.List<PropertyEntity> categoryProperties = getCategoryProperties();
        //Add unset properties
        categoryProperties.stream().forEach(categoryProperty -> {
            if (!setTags.stream().filter(t -> t.getProperty().equals(categoryProperty)).findAny().isPresent()) {
                TagEntity unsetTag = new TagEntity();
                unsetTag.setProduct(product);
                unsetTag.setProperty(categoryProperty);
//                unsetTag.setName();
                result.add(unsetTag);
            }
        });
        return result;
    }

}
