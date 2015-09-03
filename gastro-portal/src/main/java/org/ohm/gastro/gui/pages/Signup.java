package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.domain.UserEntity.Type;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.EmptyPasswordException;
import org.ohm.gastro.service.UserExistsException;
import org.ohm.gastro.service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Signup extends BaseComponent {

    @Property
    private boolean error;

    public Class onActivate() {
        if (isAuthenticated()) return Index.class;
        return null;
    }

    public Class onActivate(boolean error) {
        if (isAuthenticated()) return Index.class;
        this.error = error;
        return null;
    }

    public static void signupUser(@Nonnull String eMail,
                                  @Nonnull String fullName,
                                  @Nonnull String password,
                                  @Nonnull HttpServletRequest httpServletRequest,
                                  @Nonnull UserService userService,
                                  @Nonnull AuthenticationProvider authenticationProvider)
            throws UserExistsException, EmptyPasswordException, AuthenticationException {
        UserEntity user = new UserEntity();
        user.setEmail(eMail);
        user.setFullName(fullName);
        user.setType(Type.USER);
        user.setReferrer(Optional.ofNullable(httpServletRequest.getParameter("referrer")).map(t -> userService.findUser(Long.parseLong(t))).orElse(null));
        user = userService.createUser(user, password, null, true);
        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), password);
        token.setDetails(new WebAuthenticationDetails(httpServletRequest));
        final Authentication authentication = authenticationProvider.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}