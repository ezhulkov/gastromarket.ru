package org.ohm.gastro.service;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PurchaseEntity;
import org.ohm.gastro.domain.UserEntity;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface OrderService {

    void placeOrder(PurchaseEntity newOrder, List<ProductEntity> products);

    List<PurchaseEntity> findAllOrders(UserEntity customer, CatalogEntity catalog);

}
