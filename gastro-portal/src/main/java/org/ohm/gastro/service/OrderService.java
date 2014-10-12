package org.ohm.gastro.service;

import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PurchaseEntity;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface OrderService {

    void placeOrder(PurchaseEntity newOrder, List<ProductEntity> products);

}
