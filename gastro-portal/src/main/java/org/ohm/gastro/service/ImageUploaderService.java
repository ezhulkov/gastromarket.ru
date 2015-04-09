package org.ohm.gastro.service;

import org.ohm.gastro.domain.BaseEntity;
import org.ohm.gastro.service.ImageService.FileType;
import org.ohm.gastro.service.ImageService.ImageSize;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface ImageUploaderService<T extends BaseEntity> extends Predicate<FileType> {

    T processUploadedImages(String objectId, Map<ImageSize, String> imageUrls);

}