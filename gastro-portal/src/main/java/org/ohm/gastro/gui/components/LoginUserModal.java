package org.ohm.gastro.gui.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.UserEntity;
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

    @Property
    private String eMail;

    @Property
    private String password1;

    @Property
    private String password2;

    public Block onSubmitFromRememberForm() {
        getUserService().resetPassword(eMail);
        return rememberResultBlock;
    }

    public void onSubmitFromSignupForm() {
        System.out.println("signup");
    }

    public void signupUser(UserEntity.Type type, UserEntity user, String password1, String password2, Optional<Long> referrer) {
        if (password1 == null || password2 == null || !password1.equals(password2)) {
//            return SignupResult.PASSWORD;
        }
        user.setPassword(passwordEncoder.encode(password1));
        try {
            user.setId(null);
            user.setType(type);
            user.setReferrer(referrer.map(t -> getUserService().findUser(t)).orElse(null));
            user = getUserService().saveUser(user);
            try {
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), password1);
                token.setDetails(new WebAuthenticationDetails(getHttpServletRequest()));
                Authentication authentication = authenticationProvider.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (AuthenticationException e) {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        } catch (UserExistsException e) {
//            return SignupResult.DUPLICATE;
        } catch (EmptyPasswordException e) {
//            return SignupResult.PASSWORD;
        }
//        return SignupResult.OK;
    }


}