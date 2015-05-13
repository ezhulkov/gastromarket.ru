package org.ohm.gastro.reps;

import org.ohm.gastro.domain.PropertyEntity;
import org.ohm.gastro.domain.PropertyValueEntity;
import org.ohm.gastro.domain.PropertyValueEntity.Tag;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface PropertyValueRepository extends AltIdRepository<PropertyValueEntity> {

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PropertyValueEntity> findAllByPropertyAndRootValue(PropertyEntity property, Boolean rootValue, Sort sort);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<PropertyValueEntity> findAllByTag(Tag tag);

}
