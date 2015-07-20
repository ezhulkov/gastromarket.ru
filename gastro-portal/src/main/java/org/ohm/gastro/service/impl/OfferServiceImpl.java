package org.ohm.gastro.service.impl;

import org.ohm.gastro.domain.CatalogEntity;
import org.ohm.gastro.domain.OfferEntity;
import org.ohm.gastro.domain.PriceModifierEntity;
import org.ohm.gastro.reps.OfferRepository;
import org.ohm.gastro.reps.PriceModifierRepository;
import org.ohm.gastro.service.OfferService;
import org.ohm.gastro.trait.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by ezhulkov on 31.05.15.
 */
@Component
public class OfferServiceImpl implements OfferService, Logging {

    private final OfferRepository offerRepository;
    private final PriceModifierRepository priceModifierRepository;

    @Autowired
    public OfferServiceImpl(final OfferRepository offerRepository, PriceModifierRepository priceModifierRepository) {
        this.offerRepository = offerRepository;
        this.priceModifierRepository = priceModifierRepository;
    }

    @Override
    public List<OfferEntity> findAllOffers(final CatalogEntity catalog) {
        return offerRepository.findAllByCatalog(catalog);
    }

    @Override
    public OfferEntity findOffer(final String altId) {
        return findByAltId(altId, offerRepository);
    }

    @Override
    public OfferEntity findOffer(Long oid) {
        return offerRepository.findOne(oid);
    }

    @Override
    public void deleteOffer(final OfferEntity offer) {
        offerRepository.delete(offer);
    }

    @Override
    public OfferEntity saveOffer(final OfferEntity offer) {
        return saveWithAltId(offer, offerRepository);
    }

    @Override
    public void deleteOffer(Long oid) {
        offerRepository.delete(oid);
    }

    @Override
    public List<PriceModifierEntity> findAllModifiers(OfferEntity offer) {
        return priceModifierRepository.findAllByEntity(offer);
    }

}
