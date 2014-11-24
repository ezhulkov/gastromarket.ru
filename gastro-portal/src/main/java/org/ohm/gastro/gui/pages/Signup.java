package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.corelib.components.TextField;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.domain.UserEntity.Type;
import org.ohm.gastro.gui.components.SignupUser.SignupResult;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.Optional;

/**
 * Created by ezhulkov on 24.08.14.
 */
public abstract class Signup extends BaseComponent {

    @Property
    private SignupResult error;

    @Property
    @Persist
    private Long referrer;

    @Property
    private UserEntity newUser;

    @Property
    private String password1;

    @Property
    private String password2;

    @Component(id = "username", parameters = {"value=newUser.username", "validate=required"})
    private TextField nameField;

    @Component(id = "email", parameters = {"value=newUser.email", "validate=required"})
    private TextField emailField;

    @Component(id = "password1", parameters = {"value=password1", "validate=required"})
    private PasswordField pwdField1;

    @Component(id = "password2", parameters = {"value=password2", "validate=required"})
    private PasswordField pwdField2;

    protected abstract Type getType();

    public Object onPassivate() {
        return error == null ? null : error;
    }

    public void onActivate() {
        if (this.referrer == null) {
            String referrerStr = getHttpServletRequest().getParameter("referrer");
            this.referrer = referrerStr == null ? null : Long.parseLong(referrerStr);
        }
    }

    public void onActivate(SignupResult error) {
        this.error = error;
    }

    public void onPrepare() {
        newUser = new UserEntity();
    }

    public Class onSubmitFromSignupForm() {
        error = signupUser(getType(), newUser, password1, password2, Optional.ofNullable(referrer));
        return error.equals(SignupResult.OK) ? Index.class : null;
    }

}