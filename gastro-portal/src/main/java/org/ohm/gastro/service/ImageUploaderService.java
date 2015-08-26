package org.ohm.gastro.service;

import org.ohm.gastro.domain.BaseEntity;
import org.ohm.gastro.service.ImageService.ImageSize;

import java.util.Map;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface ImageUploaderService<T extends BaseEntity> {

    T processUploadedImages(String objectId, Map<ImageSize, String> imageUrls);

}