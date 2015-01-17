package org.ohm.gastro.service;

import org.ohm.gastro.service.ImageUploaderService.FileType;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface ImageUploaderService extends Predicate<FileType> {

    public enum ImageSize {SIZE1, SIZE2, SIZE3}

    public enum FileType {AVATAR, PRODUCT}

    void processUploadedImages(String objectId, Map<ImageSize, String> imageUrls);

}