package org.ohm.gastro.service;

import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.PhotoEntity;

import java.util.List;

/**
 * Created by ezhulkov on 09.04.15.
 */
public interface PhotoService extends ImageUploaderService {

    PhotoEntity findPhoto(Long id);

    List<PhotoEntity> findAllPhotos(OrderEntity order);

    List<PhotoEntity> findAllPhotos(CommentEntity comment);

    PhotoEntity createPhoto();

    void deletePhoto(Long id);

    public void attachPhotos(CommentEntity comment, OrderEntity order, List<PhotoEntity> submittedPhotos);

}
