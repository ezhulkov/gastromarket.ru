package org.ohm.gastro.service.impl;

import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ezhulkov on 29.04.15.
 */
@Component
public class GmAuthSuccessfulHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;

    @Autowired
    public GmAuthSuccessfulHandler(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws ServletException, IOException {
        super.onAuthenticationSuccess(request, response, authentication);
        userService.afterSuccessfulLogin((UserEntity) authentication.getPrincipal());
    }

}
