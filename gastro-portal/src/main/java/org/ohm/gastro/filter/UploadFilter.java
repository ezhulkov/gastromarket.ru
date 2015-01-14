package org.ohm.gastro.filter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.ohm.gastro.trait.Logging;

import javax.imageio.ImageIO;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by ezhulkov on 08.01.15.
 */
public class UploadFilter extends BaseApplicationFilter implements Logging {

    private final static String ajaxResponse = "{\"url\":\"%s\",\"url_small\":\"%s\"}";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {

        File file = null;
        try {

            String filePath = httpServletRequest.getParameter("file_path");
            if (StringUtils.isEmpty(filePath)) return;

            file = new File(filePath);
            BufferedImage image = ImageIO.read(file);
            BufferedImage avatarSmall = resizeImage(image, 23, 23);
            BufferedImage avatarBig = resizeImage(image, 200, 200);
            ImageIO.write(avatarSmall, "jpeg", new File("/tmp/avatar_small.jpg"));
            ImageIO.write(avatarBig, "jpeg", new File("/tmp/avatar.jpg"));

            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setHeader("Cache-Control", "no-cache");
            httpServletResponse.getWriter().write(String.format(ajaxResponse, "/img/avatar-stub.png", "/img/avatar-stub-small.png"));
            httpServletResponse.getWriter().flush();

        } catch (Exception e) {

            Logging.logger.error("", e);
            httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        } finally {

            FileUtils.deleteQuietly(file);

        }

    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, ColorSpace.TYPE_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

}
