package org.ohm.gastro.domain;

import org.ohm.gastro.util.CommonsUtils;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface AltIdEntity extends BaseEntity {

    String getName();

    String getAltId();

    void setAltId(String altId);

    default String transliterate() {
        return CommonsUtils.transliterate(getName());
    }

}