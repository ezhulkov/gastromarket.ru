package org.ohm.gastro.service.impl;

import com.google.common.collect.ImmutableMap;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.service.ImageService;
import org.ohm.gastro.service.ImageUploader;
import org.ohm.gastro.service.ImageUploaderService;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.ohm.gastro.misc.Throwables.propagate;
import static org.springframework.aop.framework.AopProxyUtils.ultimateTargetClass;

/**
 * Created by ezhulkov on 09.04.15.
 */
@Component("imageService")
@Transactional
public class ImageServiceImpl implements ImageService {

    private final static String IMAGE_NAME_TEMPLATE = "%s_%s_%s.jpg";
    private final static Map<FileType, Map<ImageSize, Integer[]>> sizes = new ImmutableMap.Builder<FileType, Map<ImageSize, Integer[]>>()
            .put(FileType.AVATAR, new ImmutableMap.Builder<ImageSize, Integer[]>()
                    .put(ImageSize.SIZE1, new Integer[]{23, 23})
                    .put(ImageSize.SIZE2, new Integer[]{100, 100})
                    .put(ImageSize.SIZE3, new Integer[]{210, 210})
                    .build())
            .put(FileType.PRODUCT, new ImmutableMap.Builder<ImageSize, Integer[]>()
                    .put(ImageSize.SIZE1, new Integer[]{140, 101})
                    .put(ImageSize.SIZE2, new Integer[]{280, 202})
                    .put(ImageSize.SIZE3, new Integer[]{560, 404})
                    .put(ImageSize.SIZE4, new Integer[]{1000, 720})
                    .build())
            .put(FileType.CATALOG, new ImmutableMap.Builder<ImageSize, Integer[]>()
                    .put(ImageSize.SIZE1, new Integer[]{23, 23})
                    .put(ImageSize.SIZE2, new Integer[]{100, 100})
                    .put(ImageSize.SIZE3, new Integer[]{270, 270})
                    .build())
            .put(FileType.PHOTO, new ImmutableMap.Builder<ImageSize, Integer[]>()
                    .put(ImageSize.SIZE1, new Integer[]{140, 101})
                    .put(ImageSize.SIZE2, new Integer[]{280, 202})
                    .put(ImageSize.SIZE3, new Integer[]{1000, 720})
                    .build())
            .put(FileType.TENDER, new ImmutableMap.Builder<ImageSize, Integer[]>()
                    .put(ImageSize.SIZE1, new Integer[]{140, 101})
                    .put(ImageSize.SIZE2, new Integer[]{280, 202})
                    .put(ImageSize.SIZE3, new Integer[]{1000, 720})
                    .build())
            .build();

    private final String imageDestinationPath;
    private final String imageDestinationUrl;
    private final ApplicationContext applicationContext;
    private Map<FileType, Optional<ImageUploaderService>> imageUploaderServiceMap;

    @Autowired
    public ImageServiceImpl(@Value("${image.dest.path}") String imageDestinationPath,
                            @Value("${image.dest.url}") String imageDestinationUrl,
                            final ApplicationContext applicationContext) {
        this.imageDestinationPath = imageDestinationPath;
        this.imageDestinationUrl = imageDestinationUrl;
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        this.imageUploaderServiceMap = applicationContext.getBeansOfType(ImageUploaderService.class)
                .values().stream()
                .collect(Collectors.toMap(bean -> ultimateTargetClass(bean).getAnnotation(ImageUploader.class).value(),
                                          Optional::of));
    }

    @Override
    public String explicitlyGetObjectId(@Nonnull final FileType fileType, final String objectIdStr, final String targetContext, @Nonnull final UserEntity caller) {
        return imageUploaderServiceMap.getOrDefault(fileType, Optional.empty()).map(bean -> bean.explicitlyGetObject(fileType, objectIdStr, targetContext, caller)).orElseGet(() -> "0");
    }

    @Override
    public Map<ImageSize, String> resizeImagePack(@Nonnull File file, @Nonnull FileType fileType, @Nullable String objectId,
                                                  @Nullable String scaleStr, @Nullable String angleStr,
                                                  @Nullable String xStr, @Nullable String yStr,
                                                  @Nullable String widthStr, @Nullable String heightStr) throws IOException {
        return resizeImagePack(new FileInputStream(file), fileType, objectId, scaleStr, angleStr, xStr, yStr, widthStr, heightStr);
    }

    @Override
    public Map<ImageSize, String> resizeImagePack(@Nonnull byte[] fileBytes, @Nonnull FileType fileType, @Nullable String objectId,
                                                  @Nullable String scaleStr, @Nullable String angleStr,
                                                  @Nullable String xStr, @Nullable String yStr,
                                                  @Nullable String widthStr, @Nullable String heightStr) throws IOException {
        return resizeImagePack(new ByteArrayInputStream(fileBytes), fileType, objectId, scaleStr, angleStr, xStr, yStr, widthStr, heightStr);
    }

    private Map<ImageSize, String> resizeImagePack(@Nonnull final InputStream is, @Nonnull final FileType fileType, @Nullable final String objectId,
                                                   @Nullable String scaleStr, @Nullable String angleStr,
                                                   @Nullable String xStr, @Nullable String yStr,
                                                   @Nullable String widthStr, @Nullable String heightStr) throws IOException {
        final BufferedImage image = ImageIO.read(is);
        Logging.logger.debug("Resizing image {}, fileType {}, objectId {} ", is, fileType, objectId);
        final Map<ImageSize, Integer[]> fileSizes = sizes.get(fileType);
        Map<ImageSize, String> imageUrls = fileSizes.entrySet().stream()
                .map(entry -> propagate(() -> {
                    ImageSize imageSize = entry.getKey();
                    final String imageName = String.format(IMAGE_NAME_TEMPLATE, fileType, objectId, imageSize);
                    final BufferedImage resizedImage = resizeImage(image, entry.getValue()[0], entry.getValue()[1], scaleStr, angleStr, xStr, yStr);
                    final String imageFileName = imageDestinationPath + File.separator + imageName;
                    ImageIO.write(resizedImage, "jpeg", new File(imageFileName));
                    Logging.logger.debug("Image resized {} to {}", imageSize, imageFileName);
                    return new Object[]{imageSize, imageDestinationUrl + imageName};
                })).collect(Collectors.toMap(t -> (ImageSize) t[0], t -> (String) t[1]));
        Logging.logger.debug("Final image set {}", imageUrls);
        imageUploaderServiceMap.getOrDefault(fileType, Optional.empty()).ifPresent(bean -> bean.processUploadedImages(fileType, objectId, imageUrls));
        return imageUrls;
    }

    public static BufferedImage resizeImage(final BufferedImage originalImage, final int width, final int height,
                                            final String scaleStr, final String angleStr,
                                            final String xStr, final String yStr) {

        //Rotate if needed
        final BufferedImage rotatedImage = angleStr == null ?
                originalImage :
                rotate(originalImage, Integer.parseInt(angleStr));

        //Calculate size
        final float originalWidth = rotatedImage.getWidth();
        final float originalHeight = rotatedImage.getHeight();
        final float croppedWidth;
        final float croppedHeight;
        final float croppedX;
        final float croppedY;
        if (angleStr == null) {
            if (originalHeight < height && originalWidth < width) {
                croppedHeight = originalHeight;
                croppedWidth = originalWidth;
            } else if (originalHeight / height == originalWidth / width) {
                croppedHeight = originalHeight;
                croppedWidth = originalWidth;
            } else if ((float) width / height > originalWidth / originalHeight) {
                if (width > originalWidth) {
                    croppedHeight = height;
                    croppedWidth = originalWidth;
                } else {
                    croppedHeight = Math.min(originalHeight, height * originalWidth / width);
                    croppedWidth = originalWidth;
                }
            } else if ((float) width / height < originalWidth / originalHeight) {
                if (height > originalHeight) {
                    croppedHeight = originalHeight;
                    croppedWidth = width;
                } else {
                    croppedHeight = originalHeight;
                    croppedWidth = Math.min(originalWidth, width * originalHeight / height);
                }
            } else {
                croppedHeight = 0;
                croppedWidth = 0;
            }
            croppedX = (originalWidth - croppedWidth) / 2;
            croppedY = (originalHeight - croppedHeight) / 2;
        } else {
            final float scale = Float.parseFloat(scaleStr);
            croppedWidth = width / scale;
            croppedHeight = height / scale;
            croppedX = Integer.parseInt(xStr) / scale;
            croppedY = Integer.parseInt(yStr) / scale;
        }

        //Crop and resize image
        final BufferedImage croppedImage = rotatedImage.getSubimage((int) croppedX,
                                                                    (int) croppedY,
                                                                    (int) croppedWidth,
                                                                    (int) croppedHeight);
        final BufferedImage resizedImage = new BufferedImage(width, height, ColorSpace.TYPE_RGB);
        final Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.drawImage(croppedImage,
                    (int) Math.max(0, (width - croppedWidth) / 2),
                    (int) Math.max(0, (height - croppedHeight) / 2),
                    (int) Math.min(croppedWidth, width),
                    (int) Math.min(croppedHeight, height),
                    null);
        g.setColor(Color.GRAY);
        g.setStroke(new BasicStroke(2));
        g.drawRect(0, 0, width - 2, height - 2);
        g.dispose();

        return resizedImage;
    }

    public static BufferedImage rotate(BufferedImage image, double angle) {
        int w = image.getWidth();
        int h = image.getHeight();
        final int neww = angle % 180 == 0 ? w : h;
        final int newh = angle % 180 == 0 ? h : w;
        final BufferedImage result = new BufferedImage(neww, newh, image.getType());
        final Graphics2D g = result.createGraphics();
        g.translate((neww - w) / 2, (newh - h) / 2);
        g.rotate(Math.toRadians(angle), w / 2, h / 2);
        g.drawRenderedImage(image, null);
        return result;
    }

}
