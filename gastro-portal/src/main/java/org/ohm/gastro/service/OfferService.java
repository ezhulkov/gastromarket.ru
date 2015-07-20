package org.ohm.gastro.service;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OfferEntity;
import org.ohm.gastro.domain.PriceModifierEntity;
import org.ohm.gastro.reps.OfferRepository;

import java.util.List;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface OfferService extends AltIdService<OfferEntity, OfferRepository> {

    List<OfferEntity> findAllOffers(CatalogEntity catalog);

    OfferEntity findOffer(String altId);

    OfferEntity findOffer(Long oid);

    void deleteOffer(OfferEntity offer);

    OfferEntity saveOffer(OfferEntity offer);

    void deleteOffer(Long oid);

    List<PriceModifierEntity> findAllModifiers(OfferEntity offer);

}
