package org.ohm.gastro.service.impl;

import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.domain.OfferEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.domain.PhotoEntity.Type;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.PhotoRepository;
import org.ohm.gastro.reps.UserRepository;
import org.ohm.gastro.service.ConversationService;
import org.ohm.gastro.service.ImageService.FileType;
import org.ohm.gastro.service.ImageService.ImageSize;
import org.ohm.gastro.service.ImageUploader;
import org.ohm.gastro.service.PhotoService;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.scribe.utils.Preconditions.checkNotNull;

/**
 * Created by ezhulkov on 09.04.15.
 */
@Component("photoService")
@ImageUploader(FileType.PHOTO)
public class PhotoServiceImpl implements PhotoService, Logging {

    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;
    private final ConversationService conversationService;

    @Autowired
    public PhotoServiceImpl(final PhotoRepository photoRepository,
                            final UserRepository userRepository,
                            final ConversationService conversationService) {
        this.photoRepository = photoRepository;
        this.userRepository = userRepository;
        this.conversationService = conversationService;
    }

    @Override
    public PhotoEntity findPhoto(final Long id) {
        return photoRepository.findOne(id);
    }

    @Override
    public List<PhotoEntity> findAllPhotos(final OfferEntity offer) {
        return photoRepository.findAllByOffer(offer);
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
    @Transactional
    public PhotoEntity createPhoto(final Type type) {
        PhotoEntity photo = new PhotoEntity();
        photo.setType(type);
        return photoRepository.save(photo);
    }

    @Override
    @Transactional
    public void deletePhoto(final Long id) {
        photoRepository.delete(id);
    }

    @Override
    @Transactional
    public void attachPhotos(final CommentEntity comment, final List<PhotoEntity> submittedPhotos) {
        attachPhotos(comment, null, null, submittedPhotos);
        comment.setPhotos(submittedPhotos);
    }

    @Override
    @Transactional
    public void attachPhotos(final OrderEntity order, final List<PhotoEntity> submittedPhotos) {
        attachPhotos(null, order, null, submittedPhotos);
        order.setPhotos(submittedPhotos);
    }

    @Override
    @Transactional
    public void attachPhotos(final OfferEntity offer, final List<PhotoEntity> submittedPhotos) {
        attachPhotos(null, null, offer, submittedPhotos);
    }

    @Override
    public List<PhotoEntity> findAllPhotos(final List<Long> ids) {
        return photoRepository.findAll(ids);
    }

    @Override
    @Transactional
    public void savePhoto(final PhotoEntity photo) {
        photoRepository.save(photo);
    }

    private void attachPhotos(final CommentEntity comment, final OrderEntity order, final OfferEntity offer, final List<PhotoEntity> submittedPhotos) {
        submittedPhotos.stream().filter(p -> p != null).forEach(photo -> {
            if (photo.getId() != null) photo = photoRepository.findOne(photo.getId());
            photo.setComment(comment);
            photo.setOrder(order);
            photo.setOffer(offer);
            photoRepository.save(photo);
        });
    }

    @Override
    @Transactional
    public void processUploadedImages(final FileType fileType, final String objectId, final Map<ImageSize, String> imageUrls) {
        checkNotNull(objectId, "ObjectId should not be null");
        final PhotoEntity photo = photoRepository.findOne(Long.parseLong(objectId));
        checkNotNull(photo, "Product should not be null " + objectId);
        photo.setAvatarUrlSmall(imageUrls.get(ImageSize.SIZE1));
        photo.setAvatarUrl(imageUrls.get(ImageSize.SIZE2));
        photo.setAvatarUrlBig(imageUrls.get(ImageSize.SIZE3));
        photoRepository.saveAndFlush(photo);
    }

    @Override
    @Transactional
    public String explicitlyGetObject(final FileType fileType, final String objectIdStr, final String targetContext, final UserEntity caller) {
        if (isNotEmpty(objectIdStr)) return objectIdStr;
        final UserEntity user = userRepository.findOne(caller.getId());
        final String[] split = targetContext.split("_");
        final UserEntity author = userRepository.findOne(Long.parseLong(split[0]));
        final UserEntity opponent = userRepository.findOne(Long.parseLong(split[1]));
        final ConversationEntity conversation = conversationService.findOrCreateConversation(author, opponent);
        return conversationService.addPhotoComment(conversation, user).getId().toString();
    }

}
