package org.ohm.gastro.service;

import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.PhotoEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by ezhulkov on 09.04.15.
 */
public interface ImageService {

    enum ImageSize {SIZE1, SIZE2, SIZE3, SIZE4}

    enum FileType {AVATAR, PRODUCT, CATALOG, ORDER, COMMENT}

    Map<ImageSize, String> resizeImagePack(@Nonnull File file, @Nonnull FileType fileType, @Nullable String objectId) throws IOException;

    Map<ImageSize, String> resizeImagePack(@Nonnull byte[] fileBytes, @Nonnull FileType fileType, @Nullable String objectId) throws IOException;

    PhotoEntity findPhoto(Long id);

    List<PhotoEntity> getPhotos(OrderEntity order);

    List<PhotoEntity> findAllPhotos(CommentEntity comment);

}
