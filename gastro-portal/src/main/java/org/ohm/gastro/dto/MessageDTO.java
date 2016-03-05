package org.ohm.gastro.dto;

import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.domain.UserEntity;

import java.util.Date;
import java.util.Optional;

/**
 * Created by ezhulkov on 03.03.16.
 */
public class MessageDTO {

    private final CommentEntity comment;
    private final UserEntity user;
    private final Optional<PhotoEntity> photo;

    public MessageDTO(CommentEntity comment, UserEntity user) {
        this.comment = comment;
        this.user = user;
        this.photo = comment.getPhotos().stream().reduce((p1, p2) -> p1);
    }

    public Long getId() {
        return comment.getId();
    }

    public String getText() {
        return comment.getTextRaw();
    }

    public UserDTO getAuthor() {
        return new UserDTO(comment.getAuthor());
    }

    public Date getDate() {
        return comment.getDate();
    }

    public String getDatePrintable() {
        return comment.getDatePrintable();
    }

    public String getRead() {
        final Date lastSeenDate = ((ConversationEntity) comment.getEntity()).getLastSeenDate(user);
        return lastSeenDate.before(comment.getDate()) ? "unread" : "read";
    }

    public String getPhotoSmall() {
        return photo.map(PhotoEntity::getAvatarUrl).orElse(null);
    }

    public String getPhotoBig() {
        return photo.map(PhotoEntity::getAvatarUrlBig).orElse(null);
    }

}
