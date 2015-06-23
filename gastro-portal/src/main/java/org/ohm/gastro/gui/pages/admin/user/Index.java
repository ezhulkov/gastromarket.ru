package org.ohm.gastro.gui.pages.admin.user;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.corelib.components.TextField;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.domain.UserEntity.Type;
import org.ohm.gastro.gui.AbstractServiceCallback;
import org.ohm.gastro.gui.ServiceCallback;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.EditObjectPage;
import org.ohm.gastro.service.EmptyPasswordException;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Index extends EditObjectPage<UserEntity> {

    @Property
    private CatalogEntity oneCatalog;

    @Property
    private CatalogEntity catalog;

    @Property
    private String newPassword;

    @Component(id = "fullName", parameters = {"value=object?.fullName", "validate=maxlength=64"})
    private TextField fnameField;

    @Component(id = "email", parameters = {"value=object?.email", "validate=maxlength=64,required,email"})
    private TextField emailField;

    @Component(id = "password", parameters = {"value=newPassword", "validate=maxlength=64"})
    private PasswordField pwdField;

    @Component(id = "catalogName", parameters = {"value=catalog?.name", "validate=maxlength=64,required"})
    private TextField catalogNameField;

    @Override
    public void activated() {
        catalog = new CatalogEntity();
    }

    @Override
    public ServiceCallback<UserEntity> getServiceCallback() {
        return new AbstractServiceCallback<UserEntity>() {

            @Override
            public UserEntity findObject(String id) {
                return getUserService().findUser(Long.parseLong(id));
            }

            @Override
            public Class<? extends BaseComponent> deleteObject(UserEntity object) {
                getUserService().toggleUser(object.getId());
                return List.class;
            }

            @Override
            public Class<? extends BaseComponent> updateObject(UserEntity user) {
                try {
                    getUserService().saveUser(user, newPassword);
                } catch (EmptyPasswordException e) {
                    getEditObject().getForm().recordError("empty password");
                }
                return Index.class;
            }

        };
    }

    @Cached
    public java.util.List<CatalogEntity> getCatalogs() {
        return getCatalogService().findAllCatalogs(getObject());
    }

    public boolean isUserCook() {
        return Type.COOK.equals(getObject().getType());
    }

    public void onSubmitFromCatalogForm() {
        catalog.setUser(getObject());
        getCatalogService().saveCatalog(catalog);
    }

    public void onActionFromDelete(Long id) {
        getCatalogService().deleteCatalog(id);
    }

}
