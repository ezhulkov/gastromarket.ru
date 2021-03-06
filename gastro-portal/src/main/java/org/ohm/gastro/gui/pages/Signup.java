package org.ohm.gastro.gui.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.domain.UserEntity.Type;
import org.ohm.gastro.filter.RegionFilter;
import org.ohm.gastro.service.EmptyPasswordException;
import org.ohm.gastro.service.UserExistsException;
import org.ohm.gastro.service.UserService;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Created by ezhulkov on 24.08.14.
 */
public class Signup extends AbstractPage {

    @Inject
    private DaoAuthenticationProvider authenticationProvider;

    @Property
    private boolean error;

    @Property
    private UserEntity referrer;

    @Property
    private String eMail;

    @Property
    private String mobilePhone;

    @Property
    private String password;

    @Property
    private String fullName;

    public Object onActivate() throws IOException {
        return onActivate(null);
    }

    public Object onActivate(Long referrerId) throws IOException {
        return onActivate(false, referrerId);
    }

    public Object onActivate(boolean error, Long referrerId) throws IOException {
        if (isAuthenticated()) return Index.class;
        this.error = error;
        if (referrerId != null) {
            referrer = getUserService().findUser(referrerId);
            getHttpServletRequest().setAttribute("referrerUser", referrer);
        }
        return true;
    }

    public Object[] onPassivate() {
        return new Object[]{error, referrer == null ? null : referrer.getId()};
    }

    public Class onSubmitFromSignupAjaxForm() {
        if (!error) {
            try {
                Signup.signupUser(eMail, mobilePhone, fullName, password, referrer, getUserService());
                return Index.class;
            } catch (UserExistsException | EmptyPasswordException e) {
                error = true;
            } catch (AuthenticationException e) {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        }
        return null;
    }

    public void onFailureFromSignupAjaxForm() {
        error = true;
    }

    public static void signupUser(@Nonnull String eMail,
                                  @Nullable String mobilePhone,
                                  @Nonnull String fullName,
                                  @Nonnull String password,
                                  @Nonnull UserEntity referrer,
                                  @Nonnull UserService userService)
            throws UserExistsException, EmptyPasswordException, AuthenticationException {
        UserEntity user = new UserEntity();
        user.setEmail(eMail);
        user.setMobilePhone(mobilePhone);
        user.setFullName(fullName);
        user.setType(Type.USER);
        user.setReferrer(referrer);
        user = userService.createUser(user, password, null, RegionFilter.getCurrentRegion(), true);
        userService.manuallyLogin(user);
    }

}