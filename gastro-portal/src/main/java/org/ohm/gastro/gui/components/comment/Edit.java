package org.ohm.gastro.gui.components.comment;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.CommentableEntity;
import org.ohm.gastro.domain.CommentableEntity.Type;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 13.08.15.
 */
public class Edit extends BaseComponent {

    @Property
    @Parameter
    private CommentEntity comment;

    @Property
    @Parameter
    private CommentableEntity entity;

    @Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String modalId;

    @Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String title;

    @Property
    @Parameter
    private boolean commentAllowed;

    @InjectComponent
    private Inject inject;

    public void onPrepareFromRateForm(Long cid) {
        inject.getSubmittedPhotos().clear();
        if (comment == null && entity != null && isAuthenticated()) {
            comment = new CommentEntity();
            comment.setEntity(entity);
            comment.setAuthor(getAuthenticatedUser());
        } else {
            comment = getRatingService().findComment(cid);
        }
    }

    public void onSuccessFromRateForm(Long cid) throws FileUploadException {
        final Map<String, byte[]> imageFiles = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(getHttpServletRequest()).stream()
                .filter(t -> t.getFieldName() != null && t.getFieldName().startsWith("imageFile"))
                .filter(t -> t.getSize() != 0)
                .collect(Collectors.toMap(FileItem::getFieldName, FileItem::get));
        if (comment.getEntity() instanceof OrderEntity) {
            getRatingService().placeTenderReply((OrderEntity) comment.getEntity(), comment, getAuthenticatedUser());
        } else {
            getRatingService().placeComment(comment.getEntity(), comment, getAuthenticatedUser());
        }
        getRatingService().attachPhotos(comment,
                                        inject.getSubmittedPhotos().stream()
                                                .map(t -> {
                                                    t.setFileBytes(imageFiles.get(String.format("imageFile%s", t.getIndex())));
                                                    return t;
                                                })
                                                .collect(Collectors.toList()));
    }

    public boolean getLike() {
        return true;
    }

    public boolean getDislike() {
        return false;
    }

    public boolean isShowRate() {
        return comment.getEntity().getCommentableType() == Type.USER || comment.getEntity().getCommentableType() == Type.CATALOG;
    }

    public String getBtnTitle() {
        return isShowRate() ? getMessages().get("comment.rate") : getMessages().get("comment.add");
    }

    public Object[] getRateFormContext() {
        return new Object[]{comment == null ? null : comment.getId()};
    }

}
