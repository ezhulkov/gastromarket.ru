package org.ohm.gastro.reps;

import org.ohm.gastro.domain.BillEntity;
import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    @Query("select p from OrderEntity p " +
            "join p.products pp " +
            "join pp.product pr " +
            "join pr.catalog c " +
            "where p.customer=:customer and (c=:catalog or :catalog is null) " +
            "order by p.date desc")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})

    List<OrderEntity> findAllByCatalogAndCustomer(@Param("customer") UserEntity customer, @Param("catalog") CatalogEntity catalog);

    @Query("select distinct p from OrderEntity p " +
            "join p.products pp " +
            "join pp.product pr " +
            "join pr.catalog c " +
            "where (c=:catalog or :catalog is null) and (p.status=:status or :status is null) " +
            "order by p.date desc")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<OrderEntity> findAllByCatalog(@Param("catalog") CatalogEntity catalog, @Param("status") OrderEntity.Status status);

    @Query("select o from OrderEntity o " +
            "join o.bill b " +
            "where b=:bill " +
            "order by o.date asc")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<OrderEntity> findAllByBill(@Param("bill") BillEntity bill);

}
