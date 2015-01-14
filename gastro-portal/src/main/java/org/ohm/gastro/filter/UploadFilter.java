package org.ohm.gastro.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ezhulkov on 08.01.15.
 */
public class UploadFilter extends BaseApplicationFilter {

    private final static String ajaxResponse = "{\"url\":\"%s\"}";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {

        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setHeader("Cache-Control", "no-cache");
        httpServletResponse.getWriter().write(String.format(ajaxResponse, "/img/cook.png"));
        httpServletResponse.getWriter().flush();

    }

}
