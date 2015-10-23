package org.ohm.gastro.service;

import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OfferEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.domain.PhotoEntity.Type;

import java.util.List;

/**
 * Created by ezhulkov on 09.04.15.
 */
public interface PhotoService extends ImageUploaderService {

    PhotoEntity findPhoto(Long id);

    List<PhotoEntity> findAllPhotos(OfferEntity offer);

    List<PhotoEntity> findAllPhotos(OrderEntity order);

    List<PhotoEntity> findAllPhotos(CommentEntity comment);

    PhotoEntity createPhoto(final Type type);

    void deletePhoto(Long id);

    void attachPhotos(CommentEntity comment, List<PhotoEntity> submittedPhotos);

    void attachPhotos(OrderEntity order, List<PhotoEntity> submittedPhotos);

    void attachPhotos(OfferEntity offer, List<PhotoEntity> submittedPhotos);

}
