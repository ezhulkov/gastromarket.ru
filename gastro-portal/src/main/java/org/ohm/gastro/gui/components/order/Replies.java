package org.ohm.gastro.gui.components.order;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
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

    @Cached
    public java.util.List<CommentEntity> getComments() {
        return getConversationService().findAllComments(order);
    }

    @Cached
    public boolean isCommentAllowed() {
        return isCook() && isDoesNotHaveReply() && isCatalogReady() && isHasOrderSlots() && isAllPaid();
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
                .filter(t -> getProductService().findProductsForFrontendCount(t) > 0)
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
    public boolean isAllPaid() {
//        return getAuthenticatedUserOpt()
//                .map(t -> getCatalogService().findAllCatalogs(t).stream())
//                .orElseGet(Stream::empty)
//                .noneMatch(t -> getBillService().findAllUnpaidBills(t).size() > 0);
        return true;
    }

}