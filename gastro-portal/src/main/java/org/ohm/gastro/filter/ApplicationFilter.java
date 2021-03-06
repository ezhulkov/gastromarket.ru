package org.ohm.gastro.filter;

import org.apache.commons.lang3.ObjectUtils;
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


        final boolean needToLog = !isStaticResource(httpServletRequest);
        final StringBuilder logStr = new StringBuilder();
        final String userAgent = (ObjectUtils.defaultIfNull(httpServletRequest.getHeader("User-Agent"), "-")).toLowerCase();
        final boolean bot = userAgent.contains("bot") || userAgent.contains("crawler") || userAgent.contains("apachebench") || userAgent.contains("slurp");

        Slf4JStopWatch stopWatch = null;

        if (needToLog) {
            final String servletPath = httpServletRequest.getServletPath();
            final Long opNumber = opCounter.incrementAndGet();
            final String referer = httpServletRequest.getHeader("Referer");
            final String sid = httpServletRequest.getSession(false) == null ? "-" : httpServletRequest.getSession(false).getId();
            final String uid = bot ? "BOT" :
                    SecurityContextHolder.getContext().getAuthentication() == null ?
                            "-" :
                            SecurityContextHolder.getContext().getAuthentication().getName();
            stopWatch = new Slf4JStopWatch("op" + opNumber, servletPath);
            MDC.put("sid", sid);
            MDC.put("ip", httpServletRequest.getHeader("X-Real-IP"));
            MDC.put("referer", referer == null || referer.startsWith("https://gastromarket") || referer.startsWith("http://localhost") ? "" : referer);
            MDC.put("uid", uid);
            MDC.put("op", "op" + Long.toString(opNumber));

            logStr.append(httpServletRequest.getMethod());
            logStr.append(" to ").append(servletPath);
            if (httpServletRequest.getQueryString() != null) {
                logStr.append("?").append(httpServletRequest.getQueryString());
            }
            if (!bot) {
                Logging.logger.info(logStr.toString());
            }

        }

        //Do filter
        if (Logging.logger.isDebugEnabled()) {
            CommonsUtils.dumpHttpServletRequest(httpServletRequest);
        }

        //Do filter
        filterChain.doFilter(httpServletRequest, httpServletResponse);

        if (httpServletResponse.getStatus() == 500 && bot) {
            Logging.logger.info("Query was {}", logStr.toString());
        }

        if (needToLog && !bot) {
            Logging.logger.debug("Request processing time (ms): " + stopWatch.getElapsedTime());
            stopWatch.stop();
        }

    }

}