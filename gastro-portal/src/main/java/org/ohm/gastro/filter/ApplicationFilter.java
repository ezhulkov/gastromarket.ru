package org.ohm.gastro.filter;

import org.ohm.gastro.trait.Logging;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class ApplicationFilter extends BaseApplicationFilter {

    private final AtomicLong opCounter = new AtomicLong(0);

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        boolean needToLog = !isStaticResource(httpServletRequest);

        Slf4JStopWatch stopWatch = null;

        if (needToLog) {
            String servletPath = httpServletRequest.getServletPath();
            Long opNumber = opCounter.incrementAndGet();
            String sid = httpServletRequest.getSession(false) == null ? "-" : httpServletRequest.getSession(false).getId();
            String uid = SecurityContextHolder.getContext().getAuthentication() == null ? "-" : SecurityContextHolder.getContext().getAuthentication().getName();
            stopWatch = new Slf4JStopWatch("op" + opNumber, servletPath);
            MDC.put("sid", sid);
            MDC.put("uid", uid);
            MDC.put("op", "op" + Long.toString(opNumber));
            StringBuilder logStr = new StringBuilder(httpServletRequest.getMethod());
            logStr.append(" to ").append(servletPath);
            if (httpServletRequest.getQueryString() != null) {
                logStr.append('?').append(httpServletRequest.getQueryString());
            }
            Logging.logger.info(logStr.toString());
        }

        //Do filter
        if (Logging.logger.isTraceEnabled()) {
            Enumeration e = httpServletRequest.getParameterNames();
            if (e != null) {
                while (e.hasMoreElements()) {
                    Object key = e.nextElement();
                    String[] values = httpServletRequest.getParameterValues((String) key);
                    Logging.logger.trace("Parameter: " + key + " Value: " + Arrays.stream(values).collect(Collectors.joining(",")));
                }
            }
        }

        //Do filter
        filterChain.doFilter(httpServletRequest, httpServletResponse);

        if (needToLog) {
            Logging.logger.debug("Request processing time (ms): " + stopWatch.getElapsedTime());
            stopWatch.stop();
        }

    }

}