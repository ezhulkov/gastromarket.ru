package org.ohm.gastro.gui.pages.office;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Checkbox;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.corelib.components.TextField;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.AbstractServiceCallback;
import org.ohm.gastro.gui.ServiceCallback;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.EditObjectPage;
import org.ohm.gastro.service.EmptyPasswordException;

import java.util.List;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Index extends EditObjectPage<UserEntity> {

    @Component(id = "fullName", parameters = {"value=object?.fullName", "validate=maxlength=64"})
    private TextField fNameField;

    @Component(id = "subscribe", parameters = {"value=object?.subscribeEmail"})
    private Checkbox subscribeField;

    @Component(id = "password1", parameters = {"value=newPassword1", "validate=maxlength=64"})
    private PasswordField p1Field;

    @Component(id = "password2", parameters = {"value=newPassword2", "validate=maxlength=64"})
    private PasswordField p2Field;

    @Property
    private String newPassword1;

    @Property
    private String newPassword2;

    @Property
    private UserEntity child;

    @Override
    public ServiceCallback<UserEntity> getServiceCallback() {
        return new AbstractServiceCallback<UserEntity>() {

            @Override
            public UserEntity findObject(String id) {
                return getAuthenticatedUser();
            }

            @Override
            public Class<? extends BaseComponent> updateObject(UserEntity user) {
                if (StringUtils.isNotEmpty(newPassword1) && StringUtils.isNotEmpty(newPassword2)) {
                    if (newPassword1.equals(newPassword2)) user.setPassword(getPasswordEncoder().encode(newPassword1));
                    else getEditObject().getForm().recordError(p1Field, getMessages().get("error.password.mismatch"));
                }
                try {
                    getUserService().saveUser(user, newPassword1);
                } catch (EmptyPasswordException e) {
                    getEditObject().getForm().recordError(p1Field, getMessages().get("error.password.empty"));
                }
                return Index.class;
            }

        };
    }

    public String getAvatarUrl() {
        return getAuthenticatedUser().getAvatarUrl();
    }

    public List<UserEntity> getChildrenUsers() {
        return getUserService().findAllChildren(getAuthenticatedUser());
    }

}
