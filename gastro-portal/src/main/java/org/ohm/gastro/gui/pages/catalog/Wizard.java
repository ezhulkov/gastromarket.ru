package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.Objects;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Wizard extends BaseComponent {

    @Property
    private CatalogEntity catalog;

    @Inject
    @Property
    private Block step1;

    @Inject
    @Property
    private Block step2;

    @Inject
    @Property
    private Block step3;

    @Inject
    @Property
    private Block step4;

    @Component
    private Form wizardForm;

    @Component(id = "desc", parameters = {"value=catalog.description", "validate=maxlength=4096,required"})
    private TextArea descField;

    @Component(id = "delivery", parameters = {"value=catalog.delivery", "validate=maxlength=4096,required"})
    private TextArea deliveryField;

    @Component(id = "payment", parameters = {"value=catalog.payment", "validate=maxlength=4096"})
    private TextArea pmtField;

    @Component(id = "basketMin", parameters = {"value=catalog.basketMin"})
    private TextField bmField;

    @Cached
    public boolean isCatalogOwner() {
        return catalog.getUser().equals(getAuthenticatedUserOpt().orElse(null));
    }

    public Object onActivate(Long pid) {
        catalog = getCatalogService().findCatalog(pid);
        if (catalog == null || !isCatalogOwner()) return new HttpError(403, "Access denied.");
        return true;
    }

    public Object[] onPassivate() {
        return new Object[]{catalog.getId()};
    }

    public Block getCurrentStepBlock() {
        if (catalog.getWizardStep() == 1) return step1;
        if (catalog.getWizardStep() == 2) return step2;
        if (catalog.getWizardStep() == 3) return step3;
        return step4;
    }

    public boolean isLastStep() {
        return Objects.equals(catalog.getWizardStep(), catalog.getMaxWizardStep());
    }

    public void onActionFromGoPrev() {
        catalog.setWizardStep(Math.max(catalog.getWizardStep() - 1, 0));
        getCatalogService().saveCatalog(catalog);
    }

    public void onSubmitFromWizardForm() {
        if (wizardForm.getHasErrors()) return;
        catalog.setWizardStep(Math.min(catalog.getWizardStep() + 1, catalog.getMaxWizardStep()));
        getCatalogService().saveCatalog(catalog);
    }

    public String getStepDescription() {
        return getMessages().get("step.desc." + catalog.getWizardStep());
    }

    public String getDescLabel() {
        return getMessages().get("desc.label." + catalog.getType().name().toLowerCase());
    }

    public String getDescText() {
        return getMessages().get("desc.text." + catalog.getType().name().toLowerCase());
    }

}