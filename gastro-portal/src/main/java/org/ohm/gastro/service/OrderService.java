package org.ohm.gastro.service;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.domain.PhotoEntity;
import org.ohm.gastro.domain.Region;
import org.ohm.gastro.domain.UserEntity;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface OrderService {

    OrderEntity placeOrder(OrderEntity preOrder, List<PhotoEntity> photos, UserEntity caller, CatalogEntity catalog);

    List<OrderEntity> findAllOrders();

    List<OrderEntity> findAllOrders(UserEntity customer);

    List<OrderEntity> findAllOrders(CatalogEntity catalog);

    List<OrderEntity> findAllOrders(CatalogEntity catalog, OrderEntity.Status status);

    List<OrderProductEntity> findAllItems(OrderEntity order);

    OrderEntity findOrder(Long id);

    OrderEntity saveOrder(OrderEntity order);

    OrderEntity saveOrder(OrderEntity order, final UserEntity caller);

    void deleteProduct(Long oid, Long pid, final UserEntity caller);

    void changeStatus(OrderEntity oneOrder, Status status, CatalogEntity catalog, final UserEntity caller);

    List<OrderEntity> findAllTenders(Region region);

    OrderEntity saveTender(OrderEntity tender, UserEntity caller);

    OrderEntity placeTender(OrderEntity tender, UserEntity caller);

    OrderEntity attachTender(CatalogEntity catalog, OrderEntity order, UserEntity caller);

    void sendTenderAnnonce(OrderEntity order);

    List<OrderEntity> findAllOrdersWithMetaStatus(final CatalogEntity catalog, final Status status);

    void cancelOrder(OrderEntity order);

    void confirmOrder(OrderEntity order);

    void closeOrder(OrderEntity order, int totalPrice, final int deliveryPrice, String survey, UserEntity caller);

    List<OrderEntity> findCommonOrders(UserEntity author, UserEntity opponent);

    List<CommentEntity> findCommonComments(UserEntity author, UserEntity opponent);

    void processCateringRequest(String name, String mobile, String eMail, String comment);

}
