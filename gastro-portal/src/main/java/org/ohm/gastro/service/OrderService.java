package org.ohm.gastro.service;

import org.ohm.gastro.domain.BillEntity;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.OrderProductEntity;
import org.ohm.gastro.domain.UserEntity;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface OrderService {

    List<OrderEntity> placeOrder(OrderEntity newOrder, List<OrderProductEntity> purchaseItems, final UserEntity customer);

    List<OrderEntity> findAllOrders(UserEntity customer, CatalogEntity catalog);

    List<OrderEntity> findAllOrders(CatalogEntity catalog);

    List<OrderEntity> findAllOrders(CatalogEntity catalog, OrderEntity.Status status);

    List<OrderEntity> findAllOrders(BillEntity bill);

    List<BillEntity> findAllBills(CatalogEntity catalog);

    int getProductsPrice(List<OrderProductEntity> products);

    OrderEntity findOrder(Long id);

    void saveOrder(OrderEntity order);

    void deleteProduct(Long oid, Long pid);

    void incProduct(Long oid, Long pid);

    void decProduct(Long oid, Long pid);

    void changeStatus(OrderEntity oneOrder, Status status);

}
