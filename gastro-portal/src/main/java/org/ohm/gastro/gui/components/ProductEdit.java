package org.ohm.gastro.gui.components;

import com.google.common.collect.Lists;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.RequestParameter;
import org.apache.tapestry5.corelib.components.Hidden;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.TagEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.product.Index;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class ProductEdit extends BaseComponent {

    public enum Stage {
        DESC() {
            @Override
            public Stage getPrevStage() {
                return null;
            }

            @Override
            public Stage getNextStage() {
                return PROP;
            }
        },
        PROP() {
            @Override
            public Stage getPrevStage() {
                return DESC;
            }

            @Override
            public Stage getNextStage() {
                return PHOTO;
            }
        },
        PHOTO() {
            @Override
            public Stage getPrevStage() {
                return PROP;
            }

            @Override
            public Stage getNextStage() {
                return null;
            }
        };

        public abstract Stage getPrevStage();

        public abstract Stage getNextStage();
    }

    private boolean goBack = false;

    @Property
    private boolean error = false;

    @Property
    private Stage stage = Stage.DESC;

    @Property
    private boolean closeImmediately;

    @Property
    private PropertyEntity oneProperty;

    @Property
    private PropertyValueEntity oneValue;

    @Property
    private TagEntity oneTag;

    @Inject
    @Property
    private Block productEditBlock;

    @Inject
    private AjaxResponseRenderer ajaxResponseRenderer;

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

    @Component(id = "stage", parameters = {"value=stage"})
    private Hidden pStageField;

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

    public String getSaveCaption() {
        if (editProduct) return getMessages().get("save.product");
        return getMessages().get("create.product");
    }

    public String getNextCaption() {
        if (stage == Stage.DESC) return editProduct ? getMessages().get("edit.props") : getMessages().get("add.props");
        if (stage == Stage.PROP) return editProduct ? getMessages().get("edit.photo") : getMessages().get("add.photo");
        return "";
    }

    public String getPrevCaption() {
        if (stage == Stage.PROP) return getMessages().get("go.back");
        return "";
    }

    public java.util.List<PropertyValueEntity> getPropertyValues() {
        return getPropertyService().findAllRootValues(oneProperty);
    }

    public String getValueType() {
        return oneProperty.getType().toString().toLowerCase();
    }

    public String getProductTagValue() {
        return editProduct ?
                getProductTags(product).stream()
                        .filter(t -> t.getProperty().equals(oneProperty))
                        .map(TagEntity::getData)
                        .findFirst().orElse("") :
                "";
    }

    public List<TagEntity> getProductListValues() {
        return editProduct ?
                getProductTags(product).stream()
                        .filter(t -> t.getProperty().getType() == PropertyEntity.Type.LIST)
                        .filter(t -> t.getProperty().equals(oneProperty))
                        .flatMap(t -> Arrays.stream(t.getData().split(", ")))
                        .map(t -> new TagEntity(t, oneProperty))
                        .collect(Collectors.toList()) :
                Lists.newArrayList();
    }

    public String getProductZone() {
        return editProduct ? "productEditZone" + product.getId() : "productZoneNew";
    }

    private void beginRender() {
        if (product == null || product.getId() == null) {
            product = new ProductEntity();
        }
    }

    public void onPrepareFromEditProductForm() {
        if (product == null || product.getId() == null) {
            product = new ProductEntity();
            product.setCatalog(catalog);
        }
    }

    public void onFailureFromEditProductForm() {
        error = true;
    }

    public void onSelectedFromSaveAndClose() {
        closeImmediately = true;
        stage = Stage.DESC;
    }

    public void onSelectedFromSaveAndNext() {
        closeImmediately = false;
    }

    public void onSelectedFromSaveAndPrev() {
        closeImmediately = false;
        goBack = true;
    }

    public Object onSubmitFromEditProductForm(Long pid, @RequestParameter(value = "stage", allowBlank = true) Stage currentStage) {
        if (!error) {
            final ProductEntity origProduct = pid != null ? getProductService().findProduct(pid) : product;
            if (currentStage == Stage.DESC) {
                origProduct.setName(product.getName());
                origProduct.setPrice(product.getPrice());
                origProduct.setDescription(product.getDescription());
                origProduct.setUnit(product.getUnit());
                origProduct.setUnitValue(product.getUnitValue());
                if (origProduct.getId() != null) product = getProductService().saveProduct(origProduct);
                else product = getProductService().createProduct(origProduct, catalog);
            } else if (currentStage == Stage.PROP) {
                Map<Long, String> propValues = getRequest().getParameterNames().stream()
                        .filter(t -> t.startsWith("prop-"))
                        .map(t -> t.substring("prop-".length(), t.length()))
                        .collect(Collectors.toMap(Long::parseLong, key -> getRequest().getParameter("prop-" + key)
                        ));
                Map<Long, String[]> listValues = getRequest().getParameterNames().stream()
                        .filter(t -> t.startsWith("list-"))
                        .map(t -> t.substring("list-".length(), t.length()))
                        .collect(Collectors.toMap(Long::parseLong, key -> getRequest().getParameters("list-" + key)
                        ));
                product = getProductService().saveProduct(origProduct, propValues, listValues);
            }
            if (goBack) this.stage = this.stage.getPrevStage();
            else this.stage = this.stage.getNextStage();
            if (closeImmediately) {
                this.stage = Stage.DESC;
                if (reloadPage) return Index.class;
            }
            if (!editProduct && closeImmediately) {
                product = new ProductEntity();
            }
            if (productsBlock != null) ajaxResponseRenderer.addRender("productsZone", productsBlock);
            if (productEditBlock != null) ajaxResponseRenderer.addRender(getProductZone(), productEditBlock);
            if (productBlock != null) ajaxResponseRenderer.addRender(productZoneId, productBlock);
        } else {
            closeImmediately = false;
            product = pid != null ? getProductService().findProduct(pid) : new ProductEntity();
            ajaxResponseRenderer.addRender(getProductZone(), productEditBlock);
        }
        return null;
    }

}