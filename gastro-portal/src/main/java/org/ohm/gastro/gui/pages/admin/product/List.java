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
import org.ohm.gastro.domain.PropertyValueEntity.Tag;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.gui.misc.GenericSelectModel;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.stream.Collectors;

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
        return oneProduct.getValues().stream()
                .map(TagEntity::getValue)
                .filter(t -> t.getTag() == Tag.ROOT || t.getParents().stream().anyMatch(p -> p.getTag() == Tag.ROOT))
                .map(AltIdBaseEntity::getName)
                .collect(Collectors.joining(";<br/>"));
    }

    public String getEvents() {
        return oneProduct.getValues().stream()
                .map(TagEntity::getValue)
                .filter(t -> t.getTag() == Tag.EVENT)
                .map(AltIdBaseEntity::getName)
                .collect(Collectors.joining(";<br/>"));
    }

    public String getIngredients() {
        return oneProduct.getValues().stream()
                .map(TagEntity::getValue)
                .filter(t -> t.getProperty().getName().equals("Ингредиенты"))
                .map(AltIdBaseEntity::getName)
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

}
