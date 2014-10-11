package org.ohm.gastro.gui.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.domain.UserEntity.Type;
import org.ohm.gastro.gui.mixins.BaseComponent;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class SignupUser extends BaseComponent {

    public enum SignupResult {
        OK, PASSWORD, DUPLICATE
    }

    @Property
    @Parameter(required = true, name = "type", allowNull = false, defaultPrefix = BindingConstants.LITERAL)
    private Type type;

    @Property
    private SignupResult error;

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

    @Inject
    private Block successBlock;

    @Inject
    private Block failureBlock;

    public void onPrepare() {
        newUser = new UserEntity();
    }

    public Block onSubmitFromSignupForm() {
        error = signupUser(type, newUser, password1, password2, null);
        return error.equals(SignupResult.OK) ? successBlock : failureBlock;
    }

}