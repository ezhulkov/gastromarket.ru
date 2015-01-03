package org.ohm.gastro.gui.pages.admin.user;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.domain.UserEntity.Type;
import org.ohm.gastro.gui.AbstractServiceCallback;
import org.ohm.gastro.gui.ServiceCallback;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.EditObjectPage;
import org.ohm.gastro.service.EmptyPasswordException;
import org.ohm.gastro.service.UserExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Index extends EditObjectPage<UserEntity> {

    @Inject
    private PasswordEncoder passwordEncoder;

    @Property
    private CatalogEntity oneCatalog;

    @Property
    private CatalogEntity catalog;

    @Property
    private String newPassword;

    @Component(id = "name", parameters = {"value=object?.username", "validate=maxlength=64,required"})
    private TextField nameField;

    @Component(id = "fullName", parameters = {"value=object?.fullName", "validate=maxlength=64"})
    private TextField fnameField;

    @Component(id = "email", parameters = {"value=object?.email", "validate=maxlength=64,required"})
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
                if (StringUtils.isNotEmpty(newPassword)) user.setPassword(passwordEncoder.encode(newPassword));
                try {
                    getUserService().saveUser(user);
                } catch (UserExistsException e) {
                    getEditObject().getForm().recordError("user exists");
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
