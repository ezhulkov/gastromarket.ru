package org.ohm.gastro.filter;

import org.apache.commons.lang.ObjectUtils;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.MailService;
import org.ohm.gastro.service.UserService;
import org.ohm.gastro.service.impl.ApplicationContextHolder;
import org.ohm.gastro.trait.Logging;
import org.ohm.gastro.util.CommonsUtils;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class ApplicationFilter extends BaseApplicationFilter {

    private final AtomicLong opCounter = new AtomicLong(0);

    @Override
    protected void doFilterInternal(final HttpServletRequest httpServletRequest,
                                    final HttpServletResponse httpServletResponse,
                                    final FilterChain filterChain) throws ServletException, IOException {

        boolean needToLog = !isStaticResource(httpServletRequest);

        Slf4JStopWatch stopWatch = null;

        if (needToLog) {
            tryLogin(httpServletRequest.getParameter("ql"));
            final String servletPath = httpServletRequest.getServletPath();
            final String userAgent = ((String) ObjectUtils.defaultIfNull(httpServletRequest.getHeader("User-Agent"), "-")).toLowerCase();
            final Long opNumber = opCounter.incrementAndGet();
            final String referer = httpServletRequest.getHeader("Referer");
            final String sid = httpServletRequest.getSession(false) == null ? "-" : httpServletRequest.getSession(false).getId();
            final String uid = userAgent.contains("bot") ? "BOT" :
                    SecurityContextHolder.getContext().getAuthentication() == null ?
                            "-" :
                            SecurityContextHolder.getContext().getAuthentication().getName();
            stopWatch = new Slf4JStopWatch("op" + opNumber, servletPath);
            MDC.put("sid", sid);
            MDC.put("ip", httpServletRequest.getHeader("X-Real-IP"));
            MDC.put("referer", referer == null || referer.startsWith("http://gastromarket") || referer.startsWith("http://localhost") ? "" : referer);
            MDC.put("uid", uid);
            MDC.put("op", "op" + Long.toString(opNumber));
            StringBuilder logStr = new StringBuilder(httpServletRequest.getMethod());
            logStr.append(" to ").append(servletPath);
            if (httpServletRequest.getQueryString() != null) {
                logStr.append('?').append(httpServletRequest.getQueryString());
            }
            Logging.logger.info(logStr.toString());
            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserEntity) {
                ApplicationContextHolder.getBean(MailService.class).cancelAllTasks((UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            }
        }

        //Do filter
        if (Logging.logger.isDebugEnabled()) {
            CommonsUtils.dumpHttpServletRequest(httpServletRequest);
        }

        //Do filter
        filterChain.doFilter(httpServletRequest, httpServletResponse);

        if (needToLog) {
            Logging.logger.debug("Request processing time (ms): " + stopWatch.getElapsedTime());
            stopWatch.stop();
        }

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