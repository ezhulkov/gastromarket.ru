package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OrderEntity;
import org.ohm.gastro.domain.OrderEntity.Status;
import org.ohm.gastro.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    @Query("select p from OrderEntity p " +
            "where p.customer=:customer and p.type=:type " +
            "order by p.date desc")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<OrderEntity> findAllByCustomerAndType(@Param("customer") UserEntity customer, @Param("type") OrderEntity.Type type);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<OrderEntity> findAllByCustomer(@Param("customer") UserEntity customer);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<OrderEntity> findAllByType(@Param("type") OrderEntity.Type type);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<OrderEntity> findAllByCatalogAndStatusAndType(@Param("catalog") CatalogEntity catalog, @Param("status") Status status, @Param("type") OrderEntity.Type type);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<OrderEntity> findAllByCatalogAndStatus(@Param("catalog") CatalogEntity catalog, @Param("status") Status status);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<OrderEntity> findAllByCatalogAndType(@Param("catalog") CatalogEntity catalog, @Param("type") OrderEntity.Type type);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<OrderEntity> findAllByCatalog(@Param("catalog") CatalogEntity catalog);

    @Query("from OrderEntity p " +
            "where p.catalog=:catalog and p.closedDate>=:from and p.closedDate<:to " +
            "order by date asc")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<OrderEntity> findAllByBill(@Param("catalog") CatalogEntity catalog, @Param("from") Date from, @Param("to") Date to);

}
