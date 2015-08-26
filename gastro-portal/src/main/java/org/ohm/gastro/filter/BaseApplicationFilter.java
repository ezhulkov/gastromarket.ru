package org.ohm.gastro.filter;

import org.ohm.gastro.trait.Logging;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public abstract class BaseApplicationFilter extends OncePerRequestFilter implements Logging {

    private static final String STATIC_RESOURCE_URLS_PARAM = "staticResourceUrls";

    private String[] urls;

    @Override
    protected void initFilterBean() throws ServletException {
        String params = super.getServletContext().getInitParameter(STATIC_RESOURCE_URLS_PARAM);
        if (params != null) {
            urls = params.split(",");
        }
        super.initFilterBean();
    }

    protected final boolean isStaticResource(HttpServletRequest request) {
        if (urls != null) {
            String servletPath = request.getServletPath();
            for (String staticUrl : urls) {
                if (servletPath.startsWith(staticUrl)) {
                    return true;
                }
            }
        }
        return false;
    }
}