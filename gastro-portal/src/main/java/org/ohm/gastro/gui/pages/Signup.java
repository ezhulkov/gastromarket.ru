package org.ohm.gastro.gui.pages;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.domain.UserEntity.Type;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.EmptyPasswordException;
import org.ohm.gastro.service.UserExistsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Signup extends BaseComponent {

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private DaoAuthenticationProvider authenticationProvider;

    @Property
    private Type type = Type.USER;

    @Property
    private String newPassword;

    @Property
    private UserEntity newUser;

    @Component(id = "username", parameters = {"value=newUser.username", "validate=required"})
    private TextField nameField;

    @Component(id = "email", parameters = {"value=newUser.email", "validate=required"})
    private TextField emailField;

    @Component(id = "password", parameters = {"value=newPassword", "validate=required"})
    private PasswordField pwdField;

    @InjectComponent
    private Form signupForm;

    public void onPrepare() {
        newUser = new UserEntity();
    }

    public void onActivate(Type type) {
        this.type = type;
    }

    public Type onPassivate() {
        return type == null || type.equals(Type.USER) ? null : type;
    }

    public Object onSubmitFromSignupForm() {
        if (StringUtils.isNotEmpty(newPassword)) newUser.setPassword(passwordEncoder.encode(newPassword));
        try {
            newUser.setType(type);
            newUser = getUserService().saveUser(newUser);
            try {
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(newUser.getUsername(), newPassword);
                token.setDetails(new WebAuthenticationDetails(getHttpServletRequest()));
                Authentication authentication = authenticationProvider.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (AuthenticationException e) {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        } catch (UserExistsException e) {
            signupForm.recordError("user exists");
            return null;
        } catch (EmptyPasswordException e) {
            signupForm.recordError("empty password");
            return null;
        }
        return Index.class;
    }

}