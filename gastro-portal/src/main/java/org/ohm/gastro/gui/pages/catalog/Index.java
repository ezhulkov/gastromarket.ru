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
import org.ohm.gastro.gui.AbstractServiceCallback;
import org.ohm.gastro.gui.ServiceCallback;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.EditObjectPage;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Index extends EditObjectPage<ProductEntity> {

    @Property
    private TagEntity oneTag;

    @Property
    private PropertyEntity oneProperty;

    @Component(id = "name", parameters = {"value=object.name", "validate=required"})
    private TextField nameField;

    @Component(id = "description", parameters = {"value=object.description"})
    private TextArea descField;

    @Component(id = "price", parameters = {"value=object.price"})
    private TextField priceField;

    @Cached
    public java.util.List<TagEntity> getProductTags() {
        return getProductTags(getObject());
    }

    public boolean isOwner() {
        UserEntity user = getAuthenticatedUser();
        return user != null && getObject().getCatalog().getUser().equals(user);
    }

    @Override
    public Object[] onPassivate() {
        return new Object[]{getObject().getId()};
    }

    @Override
    public ServiceCallback<ProductEntity> getServiceCallback() {
        return new AbstractServiceCallback<ProductEntity>() {
            @Override
            public ProductEntity findObject(String id) {
                return getCatalogService().findProduct(Long.parseLong(id));
            }

            @Override
            public Class<? extends BaseComponent> updateObject(ProductEntity product) {
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
                return Index.class;
            }
        };
    }

    public String getValueType() {
        return oneProperty.getType().toString().toLowerCase();
    }

    public java.util.List<PropertyEntity> getCategoryProperties() {
        java.util.List<PropertyEntity> allProperties = getCatalogService().findAllProperties(getObject().getCategory());
        Collections.sort(allProperties, (o1, o2) -> o1.getType().compareTo(o2.getType()));
        return allProperties;
    }

    public String getProductDescription() {
        String desc = (String) ObjectUtils.defaultIfNull(getObject().getDescription(), "");
        desc = desc.replaceAll("\\r\\n", "<br/>");
        return desc;
    }

    public java.util.List<TagEntity> getPropertyTags() {
        return getCatalogService().findAllTags(getObject(), oneProperty);
    }

}
