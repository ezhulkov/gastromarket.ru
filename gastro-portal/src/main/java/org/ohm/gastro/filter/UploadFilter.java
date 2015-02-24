package org.ohm.gastro.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.ImageUploaderService;
import org.ohm.gastro.service.ImageUploaderService.FileType;
import org.ohm.gastro.service.ImageUploaderService.ImageSize;
import org.ohm.gastro.service.impl.ApplicationContextHolder;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.ohm.gastro.misc.Throwables.propagate;

/**
 * todo add authorization
 * Created by ezhulkov on 08.01.15.
 */
public class UploadFilter extends BaseApplicationFilter implements Logging {

    private final static String IMAGE_DESTINATION_URL = "imageDestinationUrl";
    private final static String IMAGE_DESTINATION_PATH = "imageDestinationPath";
    private final static String IMAGE_NAME_TEMPLATE = "%s_%s_%s.jpg";
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private volatile Collection<ImageUploaderService> uploaderServices;

    private static String imageDestinationPath;
    private static String imageDestinationUrl;

    private final static Map<FileType, Map<ImageSize, Integer[]>> sizes = new ImmutableMap.Builder<FileType, Map<ImageSize, Integer[]>>()
            .put(FileType.AVATAR, new ImmutableMap.Builder<ImageSize, Integer[]>()
                    .put(ImageSize.SIZE1, new Integer[]{23, 23})
                    .put(ImageSize.SIZE2, new Integer[]{100, 100})
                    .put(ImageSize.SIZE3, new Integer[]{210, 210})
                    .build())
            .put(FileType.PRODUCT, new ImmutableMap.Builder<ImageSize, Integer[]>()
                    .put(ImageSize.SIZE1, new Integer[]{100, 100})
                    .put(ImageSize.SIZE2, new Integer[]{270, 270})
                    .put(ImageSize.SIZE3, new Integer[]{430, 310})
                    .build())
            .put(FileType.CATALOG, new ImmutableMap.Builder<ImageSize, Integer[]>()
                    .put(ImageSize.SIZE1, new Integer[]{23, 23})
                    .put(ImageSize.SIZE2, new Integer[]{100, 100})
                    .put(ImageSize.SIZE3, new Integer[]{210, 210})
                    .build())
            .build();

    protected void initFilterBean() throws ServletException {
        super.initFilterBean();
        imageDestinationPath = super.getServletContext().getInitParameter(IMAGE_DESTINATION_PATH);
        imageDestinationUrl = super.getServletContext().getInitParameter(IMAGE_DESTINATION_URL);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {

        File file = null;
        try {

            final List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(httpServletRequest);
            final Map<String, String> multipartParams = items.stream().filter(FileItem::isFormField).collect(Collectors.toMap(FileItem::getFieldName,
                                                                                                                              FileItem::getString));
            final String filePath = multipartParams.get("file_path");
            final String fileTypeStr = multipartParams.get("file_type");
            final String objectIdStr = multipartParams.get("object_id");

            checkNotNull(filePath, "file_path should not be empty");
            checkNotNull(fileTypeStr, "file_type should not be empty");

            file = new File(filePath);

            final FileType fileType = FileType.valueOf(fileTypeStr);
            final String objectId = fileType == FileType.AVATAR ? BaseComponent.getAuthenticatedUser(null).map(t -> t.getId().toString()).orElse("0") : objectIdStr;

            checkNotNull(objectId, "objectId should not be empty");
            checkArgument(file.exists(), "file %s should exist", filePath);

            final BufferedImage image = ImageIO.read(file);

            Logging.logger.info("Image uploaded file {}, fileType {}, objectId {} ", file, fileType, objectId);

            final Map<ImageSize, Integer[]> fileSizes = sizes.get(fileType);
            Map<ImageSize, String> imageUrls = fileSizes.entrySet().stream()
                    .map(entry -> propagate(() -> {
                        ImageSize imageSize = entry.getKey();
                        final String imageName = String.format(IMAGE_NAME_TEMPLATE, fileType, objectId, imageSize);
                        final BufferedImage resizedImage = resizeImage(image, entry.getValue()[0], entry.getValue()[1]);
                        final String imageFileName = imageDestinationPath + File.separator + imageName;
                        ImageIO.write(resizedImage, "jpeg", new File(imageFileName));
                        Logging.logger.debug("Image resized {} to {}", imageSize, imageFileName);
                        return new Object[]{imageSize, imageDestinationUrl + imageName};
                    })).collect(Collectors.toMap(t -> (ImageSize) t[0], t -> (String) t[1]));

            Logging.logger.debug("Final image set {}", imageUrls);

            getService(fileType).processUploadedImages(objectId, imageUrls);

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

    private BufferedImage resizeImage(final BufferedImage originalImage, final int width, final int height) {
        final int originalWidth = originalImage.getWidth();
        final int originalHeight = originalImage.getHeight();
        final int croppedWidth;
        final int croppedHeight;
        if (width > height) {
            croppedWidth = originalWidth;
            croppedHeight = Math.min(height * originalWidth / width, originalHeight);
        } else if (width < height) {
            croppedWidth = Math.min(width * originalHeight / height, originalWidth);
            croppedHeight = originalHeight;
        } else {
            croppedWidth = Math.min(originalHeight, originalWidth);
            croppedHeight = Math.min(originalHeight, originalWidth);
        }
        final BufferedImage croppedImage = originalImage.getSubimage((originalWidth - croppedWidth) / 2, (originalHeight - croppedHeight) / 2, croppedWidth, croppedHeight);
        final BufferedImage resizedImage = new BufferedImage(width, height, ColorSpace.TYPE_RGB);
        final Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(croppedImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;

    }

    private ImageUploaderService getService(FileType fileType) {
        if (uploaderServices == null) {
            synchronized (this) {
                if (uploaderServices == null) {
                    uploaderServices = ApplicationContextHolder.getApplicationContext().getBeansOfType(ImageUploaderService.class).values();
                }
            }
        }
        return uploaderServices.stream().filter(t -> t.test(fileType)).findFirst().orElseThrow(RuntimeException::new);
    }

}
