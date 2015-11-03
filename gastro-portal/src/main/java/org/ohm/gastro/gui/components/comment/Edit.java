package org.ohm.gastro.gui.components.comment;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.CommentableEntity;
import org.ohm.gastro.domain.CommentableEntity.Type;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;

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
    @Parameter
    private OrderEntity order;

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
    private InjectPhotos injectPhotos;

    public void onPrepareFromEditForm(Long cid, CommentableEntity.Type entityType, Long entityId) {
        injectPhotos.getSubmittedPhotos().clear();
        if (cid == null && entityType != null) {
            comment = new CommentEntity();
            comment.setEntity(getConversationService().getEntity(entityType, entityId));
            comment.setAuthor(getAuthenticatedUser());
        } else {
            comment = getConversationService().findComment(cid);
        }
    }

    public void onSuccessFromEditForm(Long cid, CommentableEntity.Type entityType, Long entityId, Long oId) throws FileUploadException {
        if (entity instanceof OrderEntity) {
            getConversationService().placeTenderReply((OrderEntity) comment.getEntity(), comment, getAuthenticatedUser());
        } else {
            if (oId != null) {
                order = getOrderService().findOrder(oId);
                if (isCook()) order.setCookRate(true);
                else if (isUser()) order.setClientRate(true);
            }
            getConversationService().placeComment(comment.getEntity(), comment, getAuthenticatedUser());
        }
        getPhotoService().attachPhotos(comment, injectPhotos.getSubmittedPhotos());
    }

    public boolean isOpenBudget() {
        return comment.getBudget() != null || entity instanceof OrderEntity && ((OrderEntity) entity).isTender() && ((OrderEntity) entity).getTotalPrice() == null;
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

    public Object[] getEditFormContext() {
        return comment == null ?
                new Object[]{null, entity.getCommentableType(), entity.getId(), order == null ? null : order.getId()} :
                new Object[]{comment.getId(), null, null};
    }

}
