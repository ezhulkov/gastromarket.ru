package org.ohm.gastro.filter;

import com.google.common.base.Charsets;
import com.maxmind.geoip.LookupService;
import org.ohm.gastro.domain.Region;
import org.ohm.gastro.misc.Throwables;
import org.ohm.gastro.trait.Logging;
import org.slf4j.MDC;

import javax.annotation.Nonnull;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class RegionFilter extends BaseApplicationFilter {

    public static final ThreadLocal<Region> REGION_THREAD_LOCAL = new ThreadLocal<>();
    private static final String STATIC_RESOURCE_URLS_PARAM = "geoIpFile";

    LookupService lookupService = null;

    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();
        final String geoIpFile = super.getServletContext().getInitParameter(STATIC_RESOURCE_URLS_PARAM);
        try {
            lookupService = new LookupService(geoIpFile, LookupService.GEOIP_INDEX_CACHE);
            Logging.logger.info("geoIpFile loaded {}", lookupService.getDatabaseInfo());
        } catch (IOException e) {
            Logging.logger.error("", e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (lookupService != null) lookupService.close();
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest httpServletRequest,
                                    final HttpServletResponse httpServletResponse,
                                    final FilterChain filterChain) throws ServletException, IOException {
        final boolean needToLog = !isStaticResource(httpServletRequest);
        if (needToLog) {
            final String qsRegion = httpServletRequest.getParameter("region");
            final String qsIp = httpServletRequest.getParameter("ip");
            final Optional<String> qsOpt = Optional.ofNullable(qsRegion);
            final Optional<String> cookieOpt = Arrays.stream(httpServletRequest.getCookies())
                    .filter(t -> t != null && t.getName().equals("region") && t.getValue() != null)
                    .map(t -> Throwables.propagate(() -> URLDecoder.decode(t.getValue(), Charsets.UTF_8.name())))
                    .findFirst();
            final Optional<String> ipOpt = Optional.ofNullable(qsIp == null ? httpServletRequest.getHeader("X-Real-IP") : qsIp);
            final String regionStr = qsOpt.orElseGet(() -> cookieOpt.orElseGet(() -> ipOpt.map(ip -> lookupService.getLocation(ip).city).orElse(Region.DEFAULT.getRegion())));
            final Region region = Region.of(regionStr);
            if (Logging.logger.isDebugEnabled()) {
                Logging.logger.debug("qsOpt: {},ipOpt: {}, cookieOpt: {}, region: {}", qsOpt.orElse("-"), ipOpt.orElse("-"), cookieOpt.orElse("-"), region);
            }
            MDC.put("originRegion", regionStr);
            MDC.put("gastroRegion", region.toString());
            httpServletRequest.getSession(true).setAttribute("region", region);
            REGION_THREAD_LOCAL.set(region);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    @Nonnull
    public static Region getCurrentRegion() {
        final Region boundRegion = REGION_THREAD_LOCAL.get();
        return boundRegion == null ? Region.DEFAULT : boundRegion;
    }

}