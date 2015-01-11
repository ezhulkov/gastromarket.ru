package org.ohm.gastro.gui.pages.admin.user;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.UserEntity;
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

    @Component(id = "email", parameters = {"value=object?.email", "validate=maxlength=64,required,email"})
    private TextField emailField;

    @Component(id = "password", parameters = {"value=newPassword", "validate=maxlength=64,required"})
    private PasswordField pwdField;

    @Component(id = "type", parameters = {"value=object?.type", "validate=required"})
    private Select typeField;

    @Cached
    public java.util.List getUsers() {
        return getUserService().findAllUser();
    }

    @Override
    public ServiceCallback<UserEntity> getServiceCallback() {
        return new AbstractServiceCallback<UserEntity>() {

            @Override
            public Class<? extends BaseComponent> addObject(UserEntity user) {
                if (StringUtils.isNotEmpty(newPassword)) user.setPassword(passwordEncoder.encode(newPassword));
                try {
                    getUserService().saveUser(user);
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

