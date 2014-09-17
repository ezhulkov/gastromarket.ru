package org.ohm.gastro.gui.pages.catalog;

import org.apache.commons.lang.ObjectUtils;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Index extends BaseComponent {

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
        getCatalogService().saveProduct(product, propValues, listValues);
    }

    public String getValueType() {
        return oneProperty.getType().toString().toLowerCase();
    }

    public java.util.List<PropertyEntity> getCategoryProperties() {
        java.util.List<PropertyEntity> allProperties = getCatalogService().findAllProperties(product.getCategory());
        Collections.sort(allProperties, (o1, o2) -> o1.getType().compareTo(o2.getType()));
        return allProperties;
    }

    public String getProductDescription() {
        String desc = (String) ObjectUtils.defaultIfNull(product.getDescription(), "");
        desc = desc.replaceAll("\\r\\n", "<br/>");
        return desc;
    }

    public java.util.List<TagEntity> getPropertyTags() {
        return getCatalogService().findAllTags(product, oneProperty);
    }

}
