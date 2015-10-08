package org.ohm.gastro.service.impl;

import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.reps.PhotoRepository;
import org.ohm.gastro.service.ImageService.FileType;
import org.ohm.gastro.service.ImageService.ImageSize;
import org.ohm.gastro.service.ImageUploader;
import org.ohm.gastro.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.scribe.utils.Preconditions.checkNotNull;

/**
 * Created by ezhulkov on 09.04.15.
 */
@Component("photoService")
@Transactional
@ImageUploader(FileType.PHOTO)
public class PhotoServiceImpl implements PhotoService {

    private final PhotoRepository photoRepository;

    @Autowired
    public PhotoServiceImpl(final PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
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

    @Override
    public PhotoEntity createPhoto() {
        return photoRepository.save(new PhotoEntity());
    }

    @Override
    public void deletePhoto(final Long id) {
        photoRepository.delete(id);
    }

    @Override
    public void attachPhotos(final CommentEntity comment, final OrderEntity order, final List<PhotoEntity> submittedPhotos) {
        submittedPhotos.forEach(photo -> {
            photo.setComment(comment);
            photo.setOrder(order);
            photoRepository.save(photo);
        });
    }

    @Override
    public void processUploadedImages(final String objectId, final Map<ImageSize, String> imageUrls) {
        checkNotNull(objectId, "ObjectId should not be null");
        final PhotoEntity photo = photoRepository.findOne(Long.parseLong(objectId));
        checkNotNull(photo, "Product should not be null");
        photo.setAvatarUrlSmall(imageUrls.get(ImageSize.SIZE1));
        photo.setAvatarUrl(imageUrls.get(ImageSize.SIZE2));
        photo.setAvatarUrlBig(imageUrls.get(ImageSize.SIZE3));
        photoRepository.saveAndFlush(photo);
    }

}
