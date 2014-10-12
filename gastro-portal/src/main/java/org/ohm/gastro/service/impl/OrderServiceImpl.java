package org.ohm.gastro.service.impl;

import org.ohm.gastro.domain.ProductEntity;
import org.ohm.gastro.domain.PurchaseEntity;
import org.ohm.gastro.domain.PurchaseProductEntity;
import org.ohm.gastro.reps.PurchaseProductRepository;
import org.ohm.gastro.reps.PurchaseRepository;
import org.ohm.gastro.reps.UserRepository;
import org.ohm.gastro.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public void placeOrder(PurchaseEntity newOrder, List<ProductEntity> products) {
        if (newOrder.getCustomer().getId() == null) userRepository.save(newOrder.getCustomer());
        List<PurchaseProductEntity> productEntities = products.stream().map(t -> {
            PurchaseProductEntity link = new PurchaseProductEntity();
            link.setPrice(t.getPrice());
            link.setOrder(newOrder);
            link.setProduct(t);
            return link;
        }).collect(Collectors.toList());
        purchaseRepository.save(newOrder);
        purchaseProductRepository.save(productEntities);
    }

}
