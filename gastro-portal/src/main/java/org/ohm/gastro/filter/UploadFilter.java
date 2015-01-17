package org.ohm.gastro.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.ohm.gastro.gui.mixins.BaseComponent;
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
import java.util.Map;
import java.util.stream.Collectors;

import static org.ohm.gastro.misc.Throwables.propagate;

/**
 * Created by ezhulkov on 08.01.15.
 */
public class UploadFilter extends BaseApplicationFilter implements Logging {

    private static enum ImageSize {SIZE1, SIZE2, SIZE3}

    private static enum FileType {AVATAR, PRODUCT}

    private final static String IMAGE_DESTINATION_URL = "imageDestinationUrl";
    private final static String IMAGE_DESTINATION_PATH = "imageDestinationPath";
    private final static String IMAGE_NAME_TEMPLATE = "%s_%s_%s.jpg";
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private static String imageDestinationPath;

    private final static Map<FileType, Map<ImageSize, Integer[]>> sizes = new ImmutableMap.Builder<FileType, Map<ImageSize, Integer[]>>()
            .put(FileType.AVATAR, new ImmutableMap.Builder<ImageSize, Integer[]>()
                    .put(ImageSize.SIZE1, new Integer[]{23, 23})
                    .put(ImageSize.SIZE2, new Integer[]{100, 100})
                    .put(ImageSize.SIZE3, new Integer[]{200, 200})
                    .build())
            .put(FileType.PRODUCT, new ImmutableMap.Builder<ImageSize, Integer[]>()
                    .put(ImageSize.SIZE1, new Integer[]{100, 100})
                    .put(ImageSize.SIZE2, new Integer[]{270, 270})
                    .put(ImageSize.SIZE3, new Integer[]{430, 310})
                    .build())
            .build();

    protected synchronized void initFilterBean() throws ServletException {
        super.initFilterBean();
        imageDestinationPath = super.getServletContext().getInitParameter(IMAGE_DESTINATION_PATH);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {

        File file = null;
        try {

            final String filePath = httpServletRequest.getParameter("file_path");
            final String fileTypeStr = httpServletRequest.getParameter("file_type");
            final String objectIdStr = httpServletRequest.getParameter("obj_id");

            Preconditions.checkNotNull(filePath, "file_path should not be empty");
            Preconditions.checkNotNull(fileTypeStr, "file_type should not be empty");

            final FileType fileType = FileType.valueOf(fileTypeStr);
            final String objectId = fileType == FileType.AVATAR ? BaseComponent.getAuthenticatedUser(null).map(t -> t.getId().toString()).orElse("0") : objectIdStr;

            Preconditions.checkNotNull(objectId, "objectId should not be empty");

            file = new File(filePath);
            final BufferedImage image = ImageIO.read(file);

            Logging.logger.info("Image uploaded fileType {}, objectId {} ", fileType, objectId);

            final Map<ImageSize, Integer[]> fileSizes = sizes.get(fileType);
            Map<String, String> imageUrls = fileSizes.entrySet().stream()
                    .map(entry -> propagate(() -> {
                        String imageSizeName = entry.getKey().toString();
                        final String imageName = String.format(IMAGE_NAME_TEMPLATE, fileType, objectId, imageSizeName);
                        final BufferedImage resizedImage = resizeImage(image, entry.getValue()[0], entry.getValue()[1]);
                        final String imageFileName = imageDestinationPath + File.separator + imageName;
                        ImageIO.write(resizedImage, "jpeg", new File(imageFileName));
                        Logging.logger.debug("Image resized {} to {}", imageSizeName, imageFileName);
                        return new String[]{imageSizeName, IMAGE_DESTINATION_URL + imageName};
                    })).collect(Collectors.toMap(t -> t[0], t -> t[1]));

            Logging.logger.debug("Final image set {}", imageUrls);

            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setHeader("Cache-Control", "no-cache");
            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(imageUrls));
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
