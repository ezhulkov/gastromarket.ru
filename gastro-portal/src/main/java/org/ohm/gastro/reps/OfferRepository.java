package org.ohm.gastro.reps;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OfferEntity;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface OfferRepository extends AltIdRepository<OfferEntity> {

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<OfferEntity> findAllByCatalog(CatalogEntity catalog);

}
