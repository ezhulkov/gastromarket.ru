package org.ohm.gastro.service;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OfferEntity;
import org.ohm.gastro.reps.OfferRepository;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface OfferService extends AltIdService<OfferEntity, OfferRepository> {

    List<OfferEntity> findAllOffers(CatalogEntity catalog);

    OfferEntity findOffer(String altId);

    void deleteOffer(OfferEntity offer);

    OfferEntity saveOffer(OfferEntity offer);

}
