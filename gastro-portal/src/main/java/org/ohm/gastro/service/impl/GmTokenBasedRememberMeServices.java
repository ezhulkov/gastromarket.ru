package org.ohm.gastro.service.impl;

import org.ohm.gastro.service.UserService;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ezhulkov on 07.04.15.
 */
public class GmTokenBasedRememberMeServices extends TokenBasedRememberMeServices implements Logging {

    private final UserService userService;

    @Autowired
    public GmTokenBasedRememberMeServices(UserService userService) {
        super("b6cNMFz1W8BFNDQPmPg2cuXJamNgCmOc", userService);
        super.setTokenValiditySeconds(3600 * 24 * 365);
        this.userService = userService;
    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {
        try {
            final UserDetails userDetails = super.processAutoLoginCookie(cookieTokens, request, response);
            if (userDetails != null) userService.afterSuccessfulLogin(userDetails);
            return userDetails;
        } catch (Exception e) {
            Logging.logger.info("Error in TokenBasedRememberMeServices {}", e.getMessage());
            throw new InvalidCookieException("");
        }
    }

}
