package org.ohm.gastro.service;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.UserEntity;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface OrderService {

    OrderEntity placeOrder(OrderEntity preOrder);

    List<OrderEntity> findAllOrders(UserEntity customer, CatalogEntity catalog);

    List<OrderEntity> findAllOrders(CatalogEntity catalog);

    List<OrderEntity> findAllOrders(CatalogEntity catalog, OrderEntity.Status status);

    int getBonuses(int price);

    OrderEntity findOrder(Long id);

    void saveOrder(OrderEntity order);

    void deleteProduct(Long oid, Long pid);

    void incProduct(Long oid, Long pid);

    void decProduct(Long oid, Long pid);

    void changeStatus(OrderEntity oneOrder, Status status, CatalogEntity catalog);

}
