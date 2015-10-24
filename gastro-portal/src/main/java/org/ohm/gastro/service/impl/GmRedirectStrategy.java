package org.ohm.gastro.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.ohm.gastro.trait.Logging;
import org.springframework.security.web.RedirectStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ezhulkov on 07.04.15.
 */
public class GmRedirectStrategy implements RedirectStrategy, Logging {

    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        final String redirectUrl = response.encodeRedirectURL(calculateRedirectUrl(request, url));
        response.sendRedirect(redirectUrl);
    }

    private String calculateRedirectUrl(HttpServletRequest request, String url) {
        final String requestURL = request.getRequestURL().toString();
        final String queryString = request.getQueryString();
        if (url == null || url.contains("/logout")) return "/";
        if (requestURL.contains("/j_security_check") || url.contains("/j_security_check") || url.contains("/login")) return url;
        if (StringUtils.isNotEmpty(queryString)) {
            return requestURL + "?" + queryString.replaceAll("ql=[a-zA-Z0-9]*(&)?", "");
        }
        return requestURL;
    }

}
