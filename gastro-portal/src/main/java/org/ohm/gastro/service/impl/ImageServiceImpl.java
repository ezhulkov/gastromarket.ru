package org.ohm.gastro.service.impl;

import com.google.common.collect.ImmutableMap;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.reps.PhotoRepository;
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
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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
                    .put(ImageSize.SIZE1, new Integer[]{100, 100})
                    .put(ImageSize.SIZE2, new Integer[]{270, 270})
                    .put(ImageSize.SIZE3, new Integer[]{560, 404})
                    .put(ImageSize.SIZE4, new Integer[]{1000, 720})
                    .build())
            .put(FileType.CATALOG, new ImmutableMap.Builder<ImageSize, Integer[]>()
                    .put(ImageSize.SIZE1, new Integer[]{23, 23})
                    .put(ImageSize.SIZE2, new Integer[]{100, 100})
                    .put(ImageSize.SIZE3, new Integer[]{270, 270})
                    .build())
            .put(FileType.ORDER, new ImmutableMap.Builder<ImageSize, Integer[]>()
                    .put(ImageSize.SIZE1, new Integer[]{100, 100})
                    .put(ImageSize.SIZE2, new Integer[]{270, 270})
                    .put(ImageSize.SIZE3, new Integer[]{1000, 720})
                    .build())
            .put(FileType.COMMENT, new ImmutableMap.Builder<ImageSize, Integer[]>()
                    .put(ImageSize.SIZE1, new Integer[]{100, 100})
                    .put(ImageSize.SIZE2, new Integer[]{270, 270})
                    .put(ImageSize.SIZE3, new Integer[]{1000, 720})
                    .build())
            .build();

    private final String imageDestinationPath;
    private final String imageDestinationUrl;
    private final ApplicationContext applicationContext;
    private final PhotoRepository photoRepository;
    private Map<FileType, Optional<ImageUploaderService>> imageUploaderServiceMap;

    @Autowired
    public ImageServiceImpl(@Value("${image.dest.path}") String imageDestinationPath,
                            @Value("${image.dest.url}") String imageDestinationUrl,
                            final ApplicationContext applicationContext,
                            final PhotoRepository photoRepository) {
        this.imageDestinationPath = imageDestinationPath;
        this.imageDestinationUrl = imageDestinationUrl;
        this.applicationContext = applicationContext;
        this.photoRepository = photoRepository;
    }

    @PostConstruct
    public void init() {
        this.imageUploaderServiceMap = applicationContext.getBeansOfType(ImageUploaderService.class)
                .values().stream()
                .collect(Collectors.toMap(bean -> ultimateTargetClass(bean).getAnnotation(ImageUploader.class).value(),
                                          Optional::of));
    }

    @Override
    public Map<ImageSize, String> resizeImagePack(@Nonnull File file, @Nonnull FileType fileType, @Nullable String objectId) throws IOException {
        return resizeImagePack(new FileInputStream(file), fileType, objectId);
    }

    @Override
    public Map<ImageSize, String> resizeImagePack(@Nonnull final byte[] fileBytes, @Nonnull final FileType fileType, @Nullable final String objectId) throws IOException {
        return resizeImagePack(new ByteArrayInputStream(fileBytes), fileType, objectId);
    }

    private Map<ImageSize, String> resizeImagePack(@Nonnull final InputStream is, @Nonnull final FileType fileType, @Nullable final String objectId) throws IOException {
        final BufferedImage image = ImageIO.read(is);
        Logging.logger.debug("Resizing image {}, fileType {}, objectId {} ", is, fileType, objectId);
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
        imageUploaderServiceMap.getOrDefault(fileType, Optional.empty()).ifPresent(bean -> bean.processUploadedImages(objectId, imageUrls));
        return imageUrls;
    }

    @Override
    public PhotoEntity findPhoto(final Long id) {
        return photoRepository.findOne(id);
    }

    @Override
    public List<PhotoEntity> findAllPhotos(final OrderEntity order) {
        return photoRepository.findAllByOrder(order);
    }

    @Override
    public List<PhotoEntity> findAllPhotos(final CommentEntity comment) {
        return photoRepository.findAllByComment(comment);
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
