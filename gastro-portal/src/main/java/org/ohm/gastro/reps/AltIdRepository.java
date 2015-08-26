package org.ohm.gastro.reps;

import org.ohm.gastro.domain.AltIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.QueryHint;

/**
 * Created by ezhulkov on 21.08.14.
 */
@NoRepositoryBean
public interface AltIdRepository<T extends AltIdEntity> extends JpaRepository<T, Long> {

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    T findByAltId(String altId);

}
