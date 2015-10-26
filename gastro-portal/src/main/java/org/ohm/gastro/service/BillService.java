package org.ohm.gastro.service;

import org.ohm.gastro.domain.BillEntity;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface BillService {

    List<BillEntity> findAllBills(CatalogEntity catalog);

    List<BillEntity> findAllUnpaidBills(CatalogEntity catalog);

    void createMissingBills(CatalogEntity catalog);

    BillEntity findBill(Long id);

    BillEntity updateBill(BillEntity bill);

    List<OrderEntity> findClosedOrders(BillEntity bill);

    List<OrderEntity> findOpenedOrders(BillEntity bill);

}
