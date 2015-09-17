package org.ohm.gastro.gui.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.gui.pages.Signup;
import org.ohm.gastro.service.EmptyPasswordException;
import org.ohm.gastro.service.UserExistsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class LoginUserModal extends BaseComponent {

    @Inject
    private DaoAuthenticationProvider authenticationProvider;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private Block rememberResultBlock;

    @Inject
    private Block signupResultBlock;

    @Inject
    @Property
    private Block signupFormBlock;

    @Property
    private String eMail;

    @Property
    private String password;

    @Property
    private String fullName;

    @Property
    private boolean passwordError = false;

    @Property
    private boolean busyError = false;

    @Property
    private boolean error = false;

    @Property
    @Parameter(name = "addText", defaultPrefix = BindingConstants.LITERAL)
    private Block addText;

    @Property
    @Parameter(name = "modalId", defaultPrefix = BindingConstants.LITERAL, value = "login")
    private String modalId;

    public Block onSubmitFromRememberAjaxForm() {
        getUserService().resetPassword(eMail);
        return rememberResultBlock;
    }

    public void onFailureFromSignupAjaxForm() {
        error = true;
    }

    public Block onSubmitFromSignupAjaxForm() {
        if (!error) {
            try {
                Signup.signupUser(eMail, fullName, password, null, getHttpServletRequest(), getUserService(), authenticationProvider);
                return signupResultBlock;
            } catch (UserExistsException e) {
                error = busyError = true;
            } catch (EmptyPasswordException e) {
                error = passwordError = true;
            } catch (AuthenticationException e) {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        }
        return signupFormBlock;
    }

}