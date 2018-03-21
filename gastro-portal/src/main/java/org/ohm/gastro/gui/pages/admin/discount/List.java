package org.ohm.gastro.gui.pages.admin.discount;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Select;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.filter.RegionFilter;
import org.ohm.gastro.gui.misc.GenericSelectModel;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.util.CommonsUtils;

import java.text.ParseException;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class List extends BaseComponent {

    @Property
    private CatalogEntity catalog;

    @Property
    private String discountFrom;

    @Property
    private String discountTo;

    @Property
    private String discountText;

    @Property
    private Integer discount;

    @Component(id = "catalogs", parameters = {"model=catalogsModel", "encoder=catalogsModel", "value=catalog", "validate=required"})
    private Select catalogsField;

    public String getRowClass() {
        return catalog.isDiscountActive() ? "discount-active" : "discount-inactive";
    }

    @Cached
    public java.util.List<CatalogEntity> getCatalogs() {
        return getCatalogService().findDiscountCatalogs().stream().sorted((c1, c2) -> c1.getDiscountFrom().compareTo(c2.getDiscountFrom())).collect(Collectors.toList());
    }

    @Cached
    public GenericSelectModel<CatalogEntity> getCatalogsModel() {
        return new GenericSelectModel<>(getCatalogService().findAllActiveCatalogs(RegionFilter.getCurrentRegion()), CatalogEntity.class, "name", "id", getAccess());
    }

    public void onSubmit() throws ParseException {
        catalog.setDiscountText(discountText);
        catalog.setDiscount(discount);
        catalog.setDiscountFrom(CommonsUtils.GUI_DATE.get().parse(discountFrom));
        catalog.setDiscountTo(CommonsUtils.GUI_DATE.get().parse(discountTo));
        getCatalogService().saveCatalog(catalog);
    }

    public void onActionFromDelete(Long id) {
        catalog = getCatalogService().findCatalog(id);
        catalog.setDiscount(null);
        catalog.setDiscountText(null);
        catalog.setDiscountFrom(null);
        catalog.setDiscountTo(null);
        getCatalogService().saveCatalog(catalog);
    }

}
