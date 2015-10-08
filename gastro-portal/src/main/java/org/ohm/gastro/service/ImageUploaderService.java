package org.ohm.gastro.service;

import org.ohm.gastro.service.ImageService.ImageSize;

import java.util.Map;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface ImageUploaderService {

    void processUploadedImages(String objectId, Map<ImageSize, String> imageUrls);

}