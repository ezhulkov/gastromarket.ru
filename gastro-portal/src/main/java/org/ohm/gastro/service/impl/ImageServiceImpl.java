package org.ohm.gastro.service.impl;

import com.google.common.collect.ImmutableMap;
import org.ohm.gastro.service.ImageService;
import org.ohm.gastro.service.ImageUploader;
import org.ohm.gastro.service.ImageUploaderService;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import static org.ohm.gastro.misc.Throwables.propagate;
import static org.springframework.aop.framework.AopProxyUtils.ultimateTargetClass;

/**
 * Created by ezhulkov on 09.04.15.
 */
@Component
public class ImageServiceImpl implements ImageService {

    private final static String IMAGE_NAME_TEMPLATE = "%s_%s_%s.jpg";
    private final static Map<FileType, Map<ImageSize, Integer[]>> sizes = new ImmutableMap.Builder<FileType, Map<ImageSize, Integer[]>>()
            .put(FileType.AVATAR, new ImmutableMap.Builder<ImageSize, Integer[]>()
                    .put(ImageSize.SIZE1, new Integer[]{23, 23})
                    .put(ImageSize.SIZE2, new Integer[]{100, 100})
                    .put(ImageSize.SIZE3, new Integer[]{210, 210})
                    .build())
            .put(FileType.PRODUCT, new ImmutableMap.Builder<ImageSize, Integer[]>()
                    .put(ImageSize.SIZE1, new Integer[]{100, 100})
                    .put(ImageSize.SIZE2, new Integer[]{270, 270})
                    .put(ImageSize.SIZE3, new Integer[]{560, 404})
                    .build())
            .put(FileType.CATALOG, new ImmutableMap.Builder<ImageSize, Integer[]>()
                    .put(ImageSize.SIZE1, new Integer[]{23, 23})
                    .put(ImageSize.SIZE2, new Integer[]{100, 100})
                    .put(ImageSize.SIZE3, new Integer[]{270, 270})
                    .build())
            .build();

    private final String imageDestinationPath;
    private final String imageDestinationUrl;
    private final ApplicationContext applicationContext;
    private Map<FileType, ImageUploaderService> imageUploaderServiceMap;

    @Autowired
    public ImageServiceImpl(@Value("${image.dest.path}") String imageDestinationPath,
                            @Value("${image.dest.url}") String imageDestinationUrl,
                            ApplicationContext applicationContext) {
        this.imageDestinationPath = imageDestinationPath;
        this.imageDestinationUrl = imageDestinationUrl;
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        this.imageUploaderServiceMap = applicationContext.getBeansOfType(ImageUploaderService.class)
                .values().stream()
                .collect(Collectors.toMap(bean -> ultimateTargetClass(bean).getAnnotation(ImageUploader.class).value(),
                                          bean -> bean));
    }

    public Map<ImageSize, String> resizeImagePack(@Nonnull File file, @Nonnull FileType fileType, @Nullable String objectId) throws IOException {

        final BufferedImage image = ImageIO.read(file);

        Logging.logger.debug("Resizing image {}, fileType {}, objectId {} ", file, fileType, objectId);

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

        imageUploaderServiceMap.get(fileType).processUploadedImages(objectId, imageUrls);

        return imageUrls;

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

}
