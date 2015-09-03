package org.ohm.gastro.gui.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
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

import java.util.Optional;

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

    public Block onSubmitFromRememberForm() {
        getUserService().resetPassword(eMail);
        return rememberResultBlock;
    }

    public void onFailureFromSignupForm() {
        error = true;
    }

    public Block onSubmitFromSignupForm() {
        if (!error) {
            try {
                UserEntity user = new UserEntity();
                user.setEmail(eMail);
                user.setFullName(fullName);
                user.setType(Type.USER);
                user.setReferrer(Optional.ofNullable(getRequest().getParameter("referrer")).map(t -> getUserService().findUser(Long.parseLong(t))).orElse(null));
                user = getUserService().createUser(user, password, null, true);
                final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), password);
                token.setDetails(new WebAuthenticationDetails(getHttpServletRequest()));
                Authentication authentication = authenticationProvider.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
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