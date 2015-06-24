package org.ohm.gastro.gui.pages.admin.user;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Checkbox;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.domain.UserEntity.Type;
import org.ohm.gastro.gui.AbstractServiceCallback;
import org.ohm.gastro.gui.ServiceCallback;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.EditObjectPage;
import org.ohm.gastro.service.EmptyPasswordException;
import org.ohm.gastro.service.UserExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class List extends EditObjectPage<UserEntity> {

    @Inject
    private PasswordEncoder passwordEncoder;

    @Property
    private UserEntity oneUser;

    @Property
    private String newPassword;

    @Property
    private boolean sendEmail = false;

    @Property
    private String catalogName;

    @Component(id = "email", parameters = {"value=object?.email", "validate=maxlength=64,required,email"})
    private TextField emailField;

    @Component(id = "fullName", parameters = {"value=object?.fullName", "validate=maxlength=64"})
    private TextField fnField;

    @Component(id = "catalogName", parameters = {"value=catalogName", "validate=maxlength=64"})
    private TextField ctgField;

    @Component(id = "sourceUrl", parameters = {"value=object?.sourceUrl", "validate=maxlength=64"})
    private TextField srcField;

    @Component(id = "password", parameters = {"value=newPassword", "validate=maxlength=64,required"})
    private PasswordField pwdField;

    @Component(id = "sendEmail", parameters = {"value=sendEmail"})
    private Checkbox sendEmailBox;

    @Cached
    public java.util.List getUsers() {
        return getUserService().findAllUser();
    }

    @Override
    public ServiceCallback<UserEntity> getServiceCallback() {
        return new AbstractServiceCallback<UserEntity>() {

            @Override
            public Class<? extends BaseComponent> addObject(UserEntity user) {
                try {
                    user.setType(Type.COOK);
                    getUserService().createUser(user, newPassword, catalogName, sendEmail);
                } catch (UserExistsException e) {
                    getEditObject().getForm().recordError(emailField, getMessages().get("error.user.exists"));
                } catch (EmptyPasswordException e) {
                    getEditObject().getForm().recordError(pwdField, getMessages().get("error.password.empty"));
                }
                return List.class;
            }

            @Override
            public UserEntity newObject() {
                return new UserEntity();
            }

        };
    }

    public void onActionFromToggleState(Long id) {
        getUserService().toggleUser(id);
    }

}

