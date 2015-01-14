package org.ohm.gastro.filter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.ohm.gastro.trait.Logging;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ezhulkov on 08.01.15.
 */
public class UploadFilter extends BaseApplicationFilter implements Logging {

    private final static String ajaxResponse = "{\"url\":\"%s\",\"url_small\":\"%s\"}";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {

        File tempFile = null;
        try {

            tempFile = File.createTempFile("gastromarket", "jpg");
            final FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            IOUtils.copy(httpServletRequest.getInputStream(), fileOutputStream);

            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setHeader("Cache-Control", "no-cache");
            httpServletResponse.getWriter().write(String.format(ajaxResponse, "/img/avatar-stub.png", "/img/avatar-stub-small.png"));
            httpServletResponse.getWriter().flush();

        } catch (Exception e) {

            Logging.logger.error("", e);
            httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        } finally {

            FileUtils.deleteQuietly(tempFile);

        }

    }

}
