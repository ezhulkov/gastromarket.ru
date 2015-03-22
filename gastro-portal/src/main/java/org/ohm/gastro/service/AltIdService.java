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
        int count = 0;
        T existingObject;
        boolean duplicate;
        do {
            String altId = String.format("%s%s", object.transliterate(), count++ == 0 ? "" : ("_" + count));
            existingObject = repository.findByAltId(altId);
            if (existingObject != null && !object.equals(existingObject)) {
                duplicate = true;
            } else {
                object.setAltId(altId);
                try {
                    repository.save(object);
                    duplicate = false;
                } catch (Exception e) {
                    logger.error("", e);
                    duplicate = true;
                }
            }
        } while (duplicate);
        return object;
    }

    default T findByAltId(String altId, R repository) {
        T object = repository.findByAltId(altId);
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