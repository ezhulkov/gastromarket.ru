package org.ohm.gastro.gui.components;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 14.05.15.
 */
public class PropertyEdit extends BaseComponent {

    @Property
    @Parameter
    private PropertyEntity property;

    @Property
    @Parameter
    private ProductEntity product;

    @Property
    @Parameter
    private String modalId;

    @Property
    private PropertyValueEntity oneValue;

    @Property
    private PropertyValueEntity oneChildValue;

    @Property
    private TagEntity oneTag;

    public java.util.List<PropertyValueEntity> getPropertyValues() {
        return getPropertyService().findAllRootValues(property);
    }

    public List<PropertyValueEntity> getParentPropertyValues() {
        return getPropertyValues().stream().filter(t -> !t.getChildren().isEmpty()).collect(Collectors.toList());
    }

    public String getValueType() {
        return property.getType().toString().toLowerCase();
    }

    public String getProductTagValue() {
        return getProductTags(product).stream()
                .filter(t -> t.getProperty().equals(property))
                .map(TagEntity::getData)
                .findFirst().orElse("");
    }

    @Cached(watch = "oneValue")
    public List<TagEntity> getProductListValues() {
        return getProductTags(product).stream()
                .filter(t -> t.getProperty().getType() == PropertyEntity.Type.LIST)
                .filter(t -> t.getProperty().equals(property))
                .flatMap(t -> Arrays.stream(t.getData().split(", ")))
                .map(t -> new TagEntity(t, property))
                .collect(Collectors.toList());
    }

}