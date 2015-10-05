package org.ohm.gastro.service;

import org.ohm.gastro.domain.BaseEntity;
import org.ohm.gastro.domain.ImageWithObject;
import org.ohm.gastro.service.ImageService.ImageSize;

import java.util.Map;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface ImageUploaderService<OBJECT extends BaseEntity, IMAGE extends BaseEntity> {

    ImageWithObject<OBJECT, IMAGE> processUploadedImages(String objectId, Map<ImageSize, String> imageUrls);

}