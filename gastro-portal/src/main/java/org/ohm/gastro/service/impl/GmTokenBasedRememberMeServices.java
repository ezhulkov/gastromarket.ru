package org.ohm.gastro.service.impl;

import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ezhulkov on 07.04.15.
 */
public class GmTokenBasedRememberMeServices extends TokenBasedRememberMeServices implements Logging {

    @Autowired
    public GmTokenBasedRememberMeServices(UserDetailsService userDetailsService) {
        super("b6cNMFz1W8BFNDQPmPg2cuXJamNgCmOc", userDetailsService);
    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {
        try {
            return super.processAutoLoginCookie(cookieTokens, request, response);
        } catch (Exception e) {
            Logging.logger.info("Error in TokenBasedRememberMeServices {}", e.getMessage());
            throw new InvalidCookieException("");
        }
    }


}
