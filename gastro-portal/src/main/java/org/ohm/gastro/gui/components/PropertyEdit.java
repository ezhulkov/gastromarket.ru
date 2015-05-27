package org.ohm.gastro.gui.components;

import com.google.common.collect.Lists;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyEntity.Type;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

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
    private TagEntity rootTag;

    private List<TagEntity> productTags;

    public void beginRender() {
        productTags = getProductService().findAllTags(product);
    }

    public java.util.List<PropertyValueEntity> getPropertyValues() {
        return getPropertyService().findAllRootValues(property);
    }

    public List<PropertyValueEntity> getRootValues() {
        return getPropertyValues().stream().filter(PropertyValueEntity::isRootValue).collect(Collectors.toList());
    }

    public List<PropertyValueEntity> getChildrenValues() {
        return getPropertyService().findAllChildrenValues(oneValue);
    }

    public String getValueType() {
        return property.getType().toString().toLowerCase();
    }

    public TagEntity getTagValue() {
        return productTags.stream()
                .filter(t -> t.getProperty().equals(property))
                .findFirst().orElse(null);
    }

    @Cached(watch = "rootTag")
    public TagEntity getChildTag() {
        return productTags.stream()
                .filter(t -> t.getProperty().getType() == Type.LIST)
                .filter(t -> t.getProperty().equals(property))
                .filter(t -> !t.getValue().isRootValue())
                .filter(t -> rootTag.getId().toString().equals(t.getData()))
                .findFirst().orElse(null);
    }

    @Cached(watch = "property")
    public List<TagEntity> getRootTags() {
        final List<TagEntity> tags = productTags.stream()
                .filter(t -> t.getProperty().getType() == Type.LIST)
                .filter(t -> t.getProperty().equals(property))
                .filter(t -> t.getValue().isRootValue())
                .sorted((o1, o2) -> o1.getId().compareTo(o2.getId()))
                .collect(Collectors.toList());
        return tags.size() > 0 ? tags : Lists.newArrayList(new TagEntity());
    }

    public boolean isRootSelected() {
        return rootTag.getId() != null && rootTag.getValue().getId().equals(oneValue.getId());
    }

    public boolean isChildSelected() {
        final TagEntity childTag = getChildTag();
        return childTag != null && rootTag.getId() != null && rootTag.getId().toString().equals(childTag.getData()) && oneChildValue.equals(childTag.getValue());
    }

    public boolean isSelectActive() {
        final TagEntity childTag = getChildTag();
        return childTag != null && rootTag.getId() != null && rootTag.getId().toString().equals(childTag.getData()) && childTag.getValue().getParents().contains(oneValue);
    }

}