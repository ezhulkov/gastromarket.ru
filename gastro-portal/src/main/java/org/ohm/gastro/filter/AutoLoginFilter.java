package org.ohm.gastro.filter;

import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.service.UserService;
import org.ohm.gastro.service.impl.ApplicationContextHolder;
import org.ohm.gastro.trait.Logging;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class AutoLoginFilter extends BaseApplicationFilter {

    @Override
    protected void doFilterInternal(final HttpServletRequest httpServletRequest,
                                    final HttpServletResponse httpServletResponse,
                                    final FilterChain filterChain) throws ServletException, IOException {

        boolean needToLog = !isStaticResource(httpServletRequest);

        if (needToLog) {
            tryLogin(httpServletRequest.getParameter("ql"));
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }

    private void tryLogin(String ql) {
        final UserService userService = ApplicationContextHolder.getBean(UserService.class);
        final MailService mailService = ApplicationContextHolder.getBean(MailService.class);
        if (ql != null && !BaseComponent.getAuthenticatedUser(userService).isPresent()) {
            try {
                final UserEntity user = mailService.parseSecuredEmail(ql);
                if (user != null) {
                    Logging.logger.info("Trying to quick login user {}", user);
                    userService.manuallyLogin(user);
                }
            } catch (Exception e) {
                Logging.logger.error("", e);
            }
        }
    }

}