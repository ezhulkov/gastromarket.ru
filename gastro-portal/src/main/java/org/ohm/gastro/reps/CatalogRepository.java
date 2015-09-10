package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface CatalogRepository extends AltIdRepository<CatalogEntity> {

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<CatalogEntity> findAllByUser(UserEntity user);

    @Query("select distinct c from CatalogEntity c join c.user u " +
            "where c.wizardStep=4 and u.status='ENABLED'" +
            "order by c.rating desc,c.id asc")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<CatalogEntity> findAllActive();

    @Query("select count(*) from CatalogEntity where rating>:rating")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    int findCatalogRank(@Param("rating") int rating);

}
