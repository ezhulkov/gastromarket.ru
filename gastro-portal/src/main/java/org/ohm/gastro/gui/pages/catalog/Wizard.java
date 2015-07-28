package org.ohm.gastro.gui.pages.catalog;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.HttpError;
import org.ohm.gastro.domain.CatalogEntity.Type;
import org.ohm.gastro.domain.ProductEntity;

import java.util.Objects;

/**
 * Created by ezhulkov on 31.08.14.
 */
public class Wizard extends AbstractCatalogPage {

    @Inject
    @Property
    private Block step1;

    @Inject
    @Property
    private Block step1Block;

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
    private Form wizardForm1;

    @Component
    private Form wizardForm2;

    @Component(id = "fullName", parameters = {"value=authenticatedUser?.fullName", "validate=maxlength=64"})
    private TextField fNameField;

    public Object onActivate(String pid) {
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

    public void onActionFromGoPrev2() {
        onActionFromGoPrev();
    }

    public void onActionFromGoPrev() {
        catalog.setWizardStep(Math.max(catalog.getWizardStep() - 1, 0));
        getCatalogService().saveCatalog(catalog);
    }

    public void onActionFromGoNext() {
        catalog.setWizardStep(Math.min(catalog.getWizardStep() + 1, catalog.getMaxWizardStep()));
        getCatalogService().saveCatalog(catalog);
    }

    public void onSubmitFromWizardForm1() {
        if (wizardForm1.getHasErrors()) return;
        catalog.setWizardStep(Math.min(catalog.getWizardStep() + 1, catalog.getMaxWizardStep()));
        getCatalogService().saveCatalog(catalog);
        getUserService().saveUser(getAuthenticatedUser());
    }

    public void onSubmitFromWizardForm2() {
        if (wizardForm2.getHasErrors()) return;
        onSubmitFromWizardForm1();
    }

    public String getStepDescription() {
        return getMessages().get("step.desc." + catalog.getWizardStep());
    }

    public Block onActionFromPrivateCook() {
        catalog.setType(Type.PRIVATE);
        getCatalogService().saveCatalog(catalog);
        return step1Block;
    }

    public Block onActionFromCompanyCook() {
        catalog.setType(Type.COMPANY);
        getCatalogService().saveCatalog(catalog);
        return step1Block;
    }

    public java.util.List<ProductEntity> getProducts() {
        return getProductService().findProductsForFrontend(null, catalog, null, null, null, 0, Integer.MAX_VALUE);
    }

}
