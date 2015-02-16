package org.ohm.gastro.service.impl;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.PurchaseEntity;
import org.ohm.gastro.domain.PurchaseProductEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.reps.PurchaseProductRepository;
import org.ohm.gastro.reps.PurchaseRepository;
import org.ohm.gastro.reps.UserRepository;
import org.ohm.gastro.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by ezhulkov on 12.10.14.
 */
@Component("orderService")
public class OrderServiceImpl implements OrderService {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseProductRepository purchaseProductRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderServiceImpl(PurchaseRepository purchaseRepository, PurchaseProductRepository purchaseProductRepository, UserRepository userRepository) {
        this.purchaseRepository = purchaseRepository;
        this.purchaseProductRepository = purchaseProductRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void placeOrder(PurchaseEntity purchase, List<PurchaseProductEntity> purchaseItems) {
        userRepository.save(purchase.getCustomer());
        purchaseRepository.save(purchase);
        purchaseItems.stream().forEach(t -> t.setPurchase(purchase));
        purchaseProductRepository.save(purchaseItems);
    }

    @Override
    public List<PurchaseEntity> findAllOrders(final UserEntity customer, final CatalogEntity catalog) {
        return purchaseRepository.findAllByCatalogAndCustomer(customer, catalog);
    }

}
