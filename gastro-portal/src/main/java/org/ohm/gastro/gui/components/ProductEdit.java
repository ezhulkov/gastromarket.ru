package org.ohm.gastro.gui.components;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.javatuples.Pair;
import org.javatuples.Tuple;
import org.javatuples.Unit;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.product.Index;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class ProductEdit extends BaseComponent {

    public enum Stage {
        DESC, PROP, PHOTO
    }

    @Inject
    @Property
    private Block editDescBlock;

    @Inject
    @Property
    private Block editPropsBlock;

    @Inject
    @Property
    private Block editPhotoBlock;

    @Property
    private boolean error = false;

    @Property
    private boolean closeImmediately;

    @Property
    private boolean propError;

    @Property
    private boolean goBack;

    @Property
    private PropertyEntity oneProperty;

    @Component(id = "productPrice", parameters = {"value=product.price", "validate=required"})
    private TextField pPriceField;

    @Component(id = "productName", parameters = {"value=product.name", "validate=maxlength=64,required"})
    private TextField pNameField;

    @Component(id = "productDescription", parameters = {"value=product.description", "validate=maxlength=1024"})
    private TextArea descField;

    @Component(id = "productUnitValue", parameters = {"value=product.unitValue", "validate=maxlength=64,min=0,max=10000"})
    private TextField pUnitValueField;

    @Component(id = "productUnit", parameters = {"value=product.unit", "validate=required"})
    private Select pUnitField;

    @Parameter(name = "catalog", required = true, allowNull = true)
    private CatalogEntity catalog;

    @Parameter(name = "reloadPage", required = false, allowNull = false, value = "false")
    private boolean reloadPage;

    @Parameter(name = "productsBlock", required = false, allowNull = false)
    private Block productsBlock;

    @Parameter(name = "productBlock", required = false, allowNull = false)
    private Block productBlock;

    @Parameter(name = "productZoneId", required = false, allowNull = false)
    private String productZoneId;

    @Property
    @Parameter(name = "modalId", defaultPrefix = BindingConstants.LITERAL, value = "pr-new")
    private String modalId;

    @Property
    @Parameter(name = "edit", defaultPrefix = BindingConstants.LITERAL, value = "true")
    private boolean editProduct;

    @Property
    @Parameter(defaultPrefix = BindingConstants.PROP, allowNull = true, required = false)
    private ProductEntity product;

    @Component(id = "descForm")
    private Form descForm;

    @Cached
    public List<PropertyEntity> getMandatoryProperties() {
        return getPropertyService().findAllProperties(true).stream().sorted((o1, o2) -> o1.getType().compareTo(o2.getType())).collect(Collectors.toList());
    }

    @Cached
    public List<PropertyEntity> getOptionalProperties() {
        return getPropertyService().findAllProperties(false).stream().sorted((o1, o2) -> o1.getType().compareTo(o2.getType())).collect(Collectors.toList());
    }

    public String getProductEditZone() {
        return editProduct ? "productEditZone" + product.getId() : "productZoneNew";
    }

    public Long getProductId() {
        return product == null || product.getId() == null ? null : product.getId();
    }

    public String getEditPropertiesLabel() {
        return editProduct ? getMessages().get("edit.properties") : getMessages().get("add.properties");
    }

    //Description section
    public void onPrepareFromDescForm() {
        if (product == null || product.getId() == null) {
            product = new ProductEntity();
            product.setCatalog(catalog);
        }
    }

    public void onFailureFromDescForm() {
        this.error = true;
    }

    public Object onSubmitFromDescForm(Long pid) {
        if (!error) {
            final ProductEntity origProduct = pid != null ? getProductService().findProduct(pid) : product;
            origProduct.setName(product.getName());
            origProduct.setPrice(product.getPrice());
            origProduct.setDescription(product.getDescription());
            origProduct.setUnit(product.getUnit());
            origProduct.setUnitValue(product.getUnitValue());
            if (origProduct.getId() != null) product = getProductService().saveProduct(origProduct);
            else product = getProductService().createProduct(origProduct, catalog);
            if (productsBlock != null) getAjaxResponseRenderer().addRender("productsZone", productsBlock);
            if (productBlock != null) getAjaxResponseRenderer().addRender(productZoneId, productBlock);
            getAjaxResponseRenderer().addRender(getProductEditZone(), closeImmediately ? editDescBlock : editPropsBlock);
        } else {
            closeImmediately = false;
            getAjaxResponseRenderer().addRender(getProductEditZone(), editDescBlock);
        }
        return null;
    }

    //Properties section
    public void onSelectedFromSaveAndBack2() {
        goBack = true;
    }

    public void onSelectedFromSaveAndClose2() {
        this.closeImmediately = true;
    }

    public Object onSubmitFromPropsForm(Long pid) {

        product = getProductService().findProduct(pid);
        final Set<Long> mandatoryIds = getMandatoryProperties().stream().map(PropertyEntity::getId).collect(Collectors.toSet());
        final Map<Long, String> propValues = getRequest().getParameterNames().stream()
                .filter(t -> t.startsWith("prop-"))
                .map(t -> t.substring("prop-".length(), t.length()))
                .filter(t -> StringUtils.isNotEmpty(getRequest().getParameter("prop-" + t)))
                .collect(Collectors.toMap(Long::parseLong, key -> getRequest().getParameter("prop-" + key), (s, a) -> s));
        final List<Tuple> listValues = getRequest().getParameterNames().stream()
                .filter(t -> t.startsWith("list-"))
                .filter(t -> ArrayUtils.isNotEmpty(getRequest().getParameters(t)) && StringUtils.isNotEmpty(getRequest().getParameters(t)[0]))
                .flatMap(t -> {
                    final String[] split = t.split("-");
                    mandatoryIds.remove(Long.parseLong(split[1]));
                    return Arrays.stream(getRequest().getParameters(t)).map(v -> {
                        final Long valueId = Long.parseLong(v);
                        final String subList = "sublist-" + valueId + "-" + split[2];
                        final String subValueId = getRequest().getParameter(subList);
                        return StringUtils.isEmpty(subValueId) ? new Unit<>(valueId) : new Pair<>(valueId, Long.parseLong(subValueId));
                    });
                }).collect(Collectors.toList());
        getProductService().saveProduct(product, propValues, listValues);
        if (!mandatoryIds.isEmpty() && !goBack) {
            propError = true;
            closeImmediately = false;
            getAjaxResponseRenderer().addRender(getProductEditZone(), editPropsBlock);
            return null;
        }
        if (getProductEditZone() != null) {
            if (goBack || closeImmediately) getAjaxResponseRenderer().addRender(getProductEditZone(), editDescBlock);
            else getAjaxResponseRenderer().addRender(getProductEditZone(), editPhotoBlock);
        }
        if (closeImmediately && !editProduct) product = null;
        return closeImmediately && reloadPage ? Index.class : null;
    }

    //Photo section
    public void onSelectedFromSaveAndBack3() {
        onSelectedFromSaveAndBack2();
    }

    public void onSelectedFromSaveAndClose3() {
        onSelectedFromSaveAndClose2();
    }

    public Object onSubmitFromPhotoForm(Long pid) {
        product = getProductService().findProduct(pid);
        if (goBack) getAjaxResponseRenderer().addRender(getProductEditZone(), editPropsBlock);
        if (closeImmediately && !editProduct) product = null;
        if (closeImmediately && getProductEditZone() != null) getAjaxResponseRenderer().addRender(getProductEditZone(), editDescBlock);
        if (productsBlock != null) getAjaxResponseRenderer().addRender("productsZone", productsBlock);
        if (productBlock != null) getAjaxResponseRenderer().addRender(productZoneId, productBlock);
        return closeImmediately && reloadPage ? Index.class : null;
    }

}