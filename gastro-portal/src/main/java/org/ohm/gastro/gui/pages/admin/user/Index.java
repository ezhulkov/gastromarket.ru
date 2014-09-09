package org.ohm.gastro.gui.pages.admin.user;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Hidden;
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
    private String origPassword;

    @Component(id = "name", parameters = {"value=object?.username", "validate=maxlength=64,required"})
    private TextField nameField;

    @Component(id = "email", parameters = {"value=object?.email", "validate=maxlength=64,required"})
    private TextField emailField;

    @Component(id = "password", parameters = {"value=object?.password", "validate=maxlength=64"})
    private PasswordField pwdField;

    @Component(id = "origPassword", parameters = {"value=origPassword"})
    private Hidden pwd2Field;

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
                UserEntity user = getUserService().findUser(Long.parseLong(id));
                origPassword = user.getPassword();
                user.setPassword(null);
                return user;
            }

            @Override
            public Class<? extends BaseComponent> deleteObject(UserEntity object) {
                getUserService().deleteUser(object.getId());
                return List.class;
            }

            @Override
            public Class<? extends BaseComponent> updateObject(UserEntity object) {
                if (StringUtils.isEmpty(object.getPassword())) object.setPassword(origPassword);
                else object.setPassword(passwordEncoder.encode(object.getPassword()));
                getUserService().saveUser(object);
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
