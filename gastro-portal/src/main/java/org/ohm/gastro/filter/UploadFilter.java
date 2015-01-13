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

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("!!!!!!!");

        httpServletResponse.sendError(HttpServletResponse.SC_OK);

    }

}
