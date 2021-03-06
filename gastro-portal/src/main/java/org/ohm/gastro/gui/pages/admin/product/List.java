package org.ohm.gastro.gui.pages.admin.product;

import com.google.common.collect.Lists;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.AltIdBaseEntity;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.PropertyValueEntity.Tag;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.gui.misc.GenericSelectModel;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class List extends BaseComponent {

    public enum Show {
        CHECKED, UNCHECKED
    }

    @Property
    private ProductEntity oneProduct;

    @Inject
    private Block editBlock;

    @Property
    private CatalogEntity catalog;

    @Property
    private Show show = Show.UNCHECKED;

    @Property
    private PropertyValueEntity value;

    @Component(id = "catalogs", parameters = {"model=catalogsModel", "encoder=catalogsModel", "value=catalog"})
    private Select catalogsField;

    @Cached
    public GenericSelectModel<CatalogEntity> getCatalogsModel() {
        return new GenericSelectModel<>(getCatalogService().findAllCatalogs(),
                                        CatalogEntity.class,
                                        "name", "id", getAccess());
    }

    public void onActivate(Long cid, Show show) {
        this.catalog = cid == null ? null : getCatalogService().findCatalog(cid);
        this.show = show;
    }

    public Object[] onPassivate() {
        return new Object[]{
                catalog == null ? null : catalog.getId(), show
        };
    }

    @Cached
    public java.util.List<ProductEntity> getProducts() {
        return getProductService().findUncheckedProducts(catalog, show == Show.CHECKED);
    }

    public String getCategories() {
        return getProductService().findAllTags(oneProduct).stream()
                .map(TagEntity::getValue)
                .filter(t -> t != null && t.getTag() != null)
                .filter(t -> t.getTag() == Tag.ROOT || t.getParents().stream().anyMatch(p -> p.getTag() == Tag.ROOT))
                .map(AltIdBaseEntity::getName)
                .distinct()
                .sorted((String::compareTo))
                .collect(Collectors.joining(";<br/>"));
    }

    public String getEvents() {
        return getProductService().findAllTags(oneProduct).stream()
                .map(TagEntity::getValue)
                .filter(t -> t != null && t.getTag() == Tag.EVENT)
                .map(AltIdBaseEntity::getName)
                .distinct()
                .sorted((String::compareTo))
                .collect(Collectors.joining(";<br/>"));
    }

    public String getIngredients() {
        return getProductService().findAllTags(oneProduct).stream()
                .map(TagEntity::getValue)
                .filter(t -> t != null)
                .filter(t -> t.getProperty().getName().equals("Ингредиенты"))
                .map(AltIdBaseEntity::getName)
                .distinct()
                .sorted((String::compareTo))
                .collect(Collectors.joining(";<br/>"));
    }

    public Object onActionFromAccept(Long id) {
        final ProductEntity product = getProductService().findProduct(id);
        product.setWasChecked(true);
        getProductService().saveProduct(product);
        return null;
    }

    public Block onActionFromEdit(Long pid) {
        this.oneProduct = getProductService().findProduct(pid);
        return editBlock;
    }

    public Object onActionFromHide(Long pid) {
        getProductService().hideProduct(pid);
        return null;
    }

    @Cached
    public java.util.List<PropertyValueEntity> getCategoryValues() {
        return getPropertyService().findAllProperties().stream().filter(t -> "Категория".equals(t.getName())).findFirst()
                .map(t -> getPropertyService().findAllRootValues(t))
                .orElseGet(Lists::newArrayList);
    }

    @Cached
    public java.util.List<PropertyValueEntity> getEventValues() {
        return getPropertyService().findAllProperties().stream().filter(t -> "Событие".equals(t.getName())).findFirst()
                .map(t -> getPropertyService().findAllRootValues(t))
                .orElseGet(Lists::newArrayList);
    }

    public void onSubmitFromProductForm() {
        final java.util.List<ProductEntity> products = getObjects("PR-", id -> getProductService().findProduct(id));
        final java.util.List<PropertyValueEntity> addCategories = getObjects("CA-ADD-", id -> getPropertyService().findPropertyValue(id));
        final java.util.List<PropertyValueEntity> addEvents = getObjects("EV-ADD-", id -> getPropertyService().findPropertyValue(id));
        final java.util.List<PropertyValueEntity> delCategories = getObjects("CA-DEL-", id -> getPropertyService().findPropertyValue(id));
        final java.util.List<PropertyValueEntity> delEvents = getObjects("EV-DEL-", id -> getPropertyService().findPropertyValue(id));
        products.stream().forEach(product -> {
            final java.util.List<TagEntity> allTags = getProductService().findAllTags(product);
            final java.util.List<PropertyValueEntity> addValues = Stream.of(addCategories, addEvents)
                    .flatMap(Collection::stream)
                    .filter(v -> allTags.stream().noneMatch(t -> v.equals(t.getValue())))
                    .collect(Collectors.toList());
            final java.util.List<PropertyValueEntity> delValues = Stream.of(delCategories, delEvents)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            getProductService().saveProduct(product, addValues, delValues);
        });
    }

    private <T> java.util.List<T> getObjects(String prefix, Function<Long, T> f) {
        return getRequest().getParameterNames().stream()
                .filter(t -> t.startsWith(prefix))
                .map(t -> t.substring(prefix.length(), t.length()))
                .map(Long::parseLong)
                .map(f::apply)
                .collect(Collectors.toList());
    }

}
