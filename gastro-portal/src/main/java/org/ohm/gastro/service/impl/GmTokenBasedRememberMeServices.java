package org.ohm.gastro.service.impl;

import org.apache.commons.lang3.ObjectUtils;
import org.ohm.gastro.service.UserService;
import org.ohm.gastro.trait.Logging;
import org.ohm.gastro.util.CommonsUtils;
import org.slf4j.MDC;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * Created by ezhulkov on 07.04.15.
 */
public class GmTokenBasedRememberMeServices extends TokenBasedRememberMeServices implements Logging {

    public GmTokenBasedRememberMeServices(String key, UserDetailsService userDetailsService) {
        super(key, userDetailsService);
    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {
        MDC.clear();
        return Optional.ofNullable(request.getParameter("ql"))
                .map(this::tryLogin)
                .orElseGet(() -> super.processAutoLoginCookie(cookieTokens, request, response));
    }

    private UserDetails tryLogin(String ql) {
        return CommonsUtils.parseSecuredEmail(ql).map(t -> ApplicationContextHolder.getBean(UserService.class).findUser(t)).orElse(null);
    }

    @Override
    protected String extractRememberMeCookie(HttpServletRequest request) {
        return ObjectUtils.defaultIfNull(request.getParameter("ql"), super.extractRememberMeCookie(request));
    }

}
