package org.ohm.gastro.service;

import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.service.ImageService.FileType;
import org.ohm.gastro.service.ImageService.ImageSize;

import java.util.Map;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface ImageUploaderService {

    void processUploadedImages(final FileType fileType, String objectId, Map<ImageSize, String> imageUrls);

    default String explicitlyGetObject(FileType fileType, String objectIdStr, String targetContext, UserEntity caller) {
        return objectIdStr;
    }

}