package org.ohm.gastro.gui.pages.admin.product;

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
        return new GenericSelectModel<>(getCatalogService().findAllActiveCatalogs(),
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
                .collect(Collectors.joining(";<br/>"));
    }

    public String getEvents() {
        return getProductService().findAllTags(oneProduct).stream()
                .map(TagEntity::getValue)
                .filter(t -> t != null && t.getTag() != null)
                .filter(t -> t.getTag() == Tag.EVENT)
                .map(AltIdBaseEntity::getName)
                .distinct()
                .collect(Collectors.joining(";<br/>"));
    }

    public String getIngredients() {
        return getProductService().findAllTags(oneProduct).stream()
                .map(TagEntity::getValue)
                .filter(t -> t != null)
                .filter(t -> t.getProperty().getName().equals("Ингредиенты"))
                .map(AltIdBaseEntity::getName)
                .distinct()
                .collect(Collectors.joining(";<br/>"));
    }

    public void onActionFromAccept(Long id) {
        final ProductEntity product = getProductService().findProduct(id);
        product.setWasChecked(true);
        getProductService().saveProduct(product);
    }

    public Block onActionFromEdit(Long pid) {
        this.oneProduct = getProductService().findProduct(pid);
        return editBlock;
    }

    public void onActionFromHide(Long pid) {
        getProductService().hideProduct(pid);
    }

    @Cached
    public java.util.List<PropertyValueEntity> getCategoryValues() {
        return getPropertyService().findAllValues(Tag.ROOT);
    }

    @Cached
    public java.util.List<PropertyValueEntity> getEventValues() {
        return getPropertyService().findAllValues(Tag.EVENT);
    }

    public void onSubmitFromProductForm() {
        final java.util.List<ProductEntity> products = getObjects("PR-", id -> getProductService().findProduct(id));
        final java.util.List<PropertyValueEntity> categories = getObjects("CA-", id -> getPropertyService().findPropertyValue(id));
        final java.util.List<PropertyValueEntity> events = getObjects("EV-", id -> getPropertyService().findPropertyValue(id));
        products.stream().forEach(product -> {
            final java.util.List<TagEntity> allTags = getProductService().findAllTags(product);
            final java.util.List<PropertyValueEntity> newValues = Stream.of(categories, events)
                    .flatMap(Collection::stream)
                    .filter(v -> allTags.stream().noneMatch(t -> v.equals(t.getValue())))
                    .collect(Collectors.toList());
            getProductService().saveProduct(product, newValues);
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
