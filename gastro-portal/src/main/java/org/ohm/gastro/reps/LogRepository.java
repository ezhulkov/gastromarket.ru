package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.LogEntity;
import org.ohm.gastro.domain.LogEntity.Type;
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
public interface LogRepository extends JpaRepository<LogEntity, Long> {

    @Query("from LogEntity where user=:user and type=:type and date>=:date")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<LogEntity> findAll(@Param("user") UserEntity user, @Param("date") Date dateFrom, @Param("type") Type type);

    @Query("from LogEntity where user=:user and catalog=:catalog and date>=:date")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<LogEntity> findAll(@Param("user") UserEntity user, @Param("catalog") CatalogEntity catalog, @Param("date") Date dateFrom);

}
