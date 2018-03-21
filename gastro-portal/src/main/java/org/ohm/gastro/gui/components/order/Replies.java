package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OrderEntity.Status;

import java.util.stream.Stream;

/**
 * Created by ezhulkov on 31.07.15.
 */
public class Replies extends AbstractOrder {

    @Inject
    @Property
    private Block replyBlock;

    @Property
    private CommentEntity oneComment;

    @Property
    @Parameter
    protected boolean reply;

    @Property
    private boolean showReplies;

    @InjectComponent
    private Zone repliesZone;

    public void beginRender() {
        showReplies = !order.isOrderAttached();
    }

    @Cached
    public java.util.List<CommentEntity> getComments() {
        return getConversationService().findAllComments(order);
    }

    @Cached
    public boolean isCommentAllowed() {
        return isCook() && isDoesNotHaveReply() && isCatalogReady() && isHasOrderSlots() && isContractSigned();
    }

    @Cached
    public boolean isDoesNotHaveReply() {
        return getAuthenticatedUserOpt()
                .map(user -> getConversationService().findAllComments(order, user).size() == 0)
                .orElse(false);
    }

    @Cached
    public boolean isCatalogReady() {
        return getAuthenticatedUserOpt()
                .map(t -> getCatalogService().findAllCatalogs(t).stream())
                .orElseGet(Stream::empty)
                .filter(CatalogEntity::isWasSetup)
                .filter(t -> getProductService().findProductsForFrontendCount(t) >= 5)
                .findFirst()
                .isPresent();
    }

    @Cached
    public boolean isHasOrderSlots() {
        return getAuthenticatedUserOpt()
                .map(t -> getCatalogService().findAllCatalogs(t).stream())
                .orElseGet(Stream::empty)
                .filter(t -> t.getLevel() != null && t.getLevel() > getOrderService().findAllOrdersWithMetaStatus(t, Status.ACTIVE).size())
                .findAny()
                .isPresent();
    }

    @Cached
    public boolean isContractSigned() {
        return getAuthenticatedUserOpt()
                .map(t -> getCatalogService().findAllCatalogs(t).stream())
                .orElseGet(Stream::empty)
                .filter(t -> !t.getContractSigned())
                .count() == 0;
    }

    public Block onActionFromShowRepliesAjaxLink(Long oid) {
        this.order = getOrderService().findOrder(oid);
        this.showReplies = true;
        return repliesZone.getBody();
    }

    public Block onActionFromHideRepliesAjaxLink(Long oid) {
        this.order = getOrderService().findOrder(oid);
        this.showReplies = false;
        return repliesZone.getBody();
    }

}