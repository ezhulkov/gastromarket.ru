package org.ohm.gastro.filter;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.ohm.gastro.trait.Logging;
import org.ohm.gastro.util.CommonsUtils;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

        final HttpServletRequest chainHttpServletRequest = needToLog ? new MultipleReadHttpServletRequest(httpServletRequest) : httpServletRequest;
        if (needToLog) {
            final String servletPath = chainHttpServletRequest.getServletPath();
            final String userAgent = ((String) ObjectUtils.defaultIfNull(chainHttpServletRequest.getHeader("User-Agent"), "-")).toLowerCase();
            final Long opNumber = opCounter.incrementAndGet();
            final String referer = chainHttpServletRequest.getHeader("Referer");
            final String sid = chainHttpServletRequest.getSession(false) == null ? "-" : chainHttpServletRequest.getSession(false).getId();
            final String uid = userAgent.contains("bot") ? "BOT" :
                    SecurityContextHolder.getContext().getAuthentication() == null ?
                            "-" :
                            SecurityContextHolder.getContext().getAuthentication().getName();
            stopWatch = new Slf4JStopWatch("op" + opNumber, servletPath);
            MDC.put("sid", sid);
            MDC.put("ip", chainHttpServletRequest.getHeader("X-Real-IP"));
            MDC.put("referer", referer == null || referer.startsWith("http://gastromarket") || referer.startsWith("http://localhost") ? "" : referer);
            MDC.put("uid", uid);
            MDC.put("op", "op" + Long.toString(opNumber));
            StringBuilder logStr = new StringBuilder(chainHttpServletRequest.getMethod());
            logStr.append(" to ").append(servletPath);
            if (chainHttpServletRequest.getQueryString() != null) {
                logStr.append('?').append(chainHttpServletRequest.getQueryString());
            }
            Logging.logger.info(logStr.toString());
        }

        //Do filter
        if (Logging.logger.isDebugEnabled()) {
            CommonsUtils.dumpHttpServletRequest(chainHttpServletRequest);
        }

        //Do filter
        filterChain.doFilter(chainHttpServletRequest, httpServletResponse);

        if (needToLog) {
            Logging.logger.debug("Request processing time (ms): " + stopWatch.getElapsedTime());
            stopWatch.stop();
        }

    }

    private class MultipleReadHttpServletRequest extends HttpServletRequestWrapper {

        private byte[] body;

        public MultipleReadHttpServletRequest(final HttpServletRequest request) {
            super(request);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (body == null) {
                synchronized (this) {
                    if (body == null) body = IOUtils.toByteArray(super.getInputStream());
                }
            }
            final InputStream is = new ByteArrayInputStream(body);
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return true;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(final ReadListener readListener) {

                }

                @Override
                public int read() throws IOException {
                    return is.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream(), (String) ObjectUtils.defaultIfNull(getCharacterEncoding(), Charsets.UTF_8.name())));
        }

    }

}