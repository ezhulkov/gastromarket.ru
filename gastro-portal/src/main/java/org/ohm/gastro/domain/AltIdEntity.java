package org.ohm.gastro.domain;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface AltIdEntity extends BaseEntity {

    String getName();

    String getAltId();

    void setAltId(String altId);

    String transliterate();

}