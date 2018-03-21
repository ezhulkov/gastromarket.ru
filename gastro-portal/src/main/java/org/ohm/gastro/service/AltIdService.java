package org.ohm.gastro.service;

import org.ohm.gastro.domain.AltIdEntity;
import org.ohm.gastro.reps.AltIdRepository;
import org.ohm.gastro.trait.Logging;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface AltIdService<T extends AltIdEntity, R extends AltIdRepository<T>> extends Logging {

    @Transactional(propagation = Propagation.MANDATORY)
    default T saveWithAltId(T object, R repository) {
        if (object.getId() == null) repository.save(object);
        final StringBuilder altId = new StringBuilder(object.transliterate());
        if (repository.findAllByAltId(altId.toString()).stream().filter(t -> !t.equals(object)).count() > 0) altId.append("-").append(object.getId());
        object.setAltId(altId.toString());
        return repository.save(object);
    }

    default T findByAltId(String altId, R repository) {
        T object = repository.findAllByAltId(altId).stream().findFirst().orElse(null);
        if (object == null) {
            try {
                long cid = Long.parseLong(altId);
                object = repository.findOne(cid);
            } catch (NumberFormatException e) {
                logger.debug("Not a number");
            }
        }
        return object;
    }

}