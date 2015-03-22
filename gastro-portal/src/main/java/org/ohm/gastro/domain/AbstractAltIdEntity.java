package org.ohm.gastro.domain;

/**
 * Created by ezhulkov on 24.08.14.
 */
public abstract class AbstractAltIdEntity extends AbstractBaseEntity implements AltIdEntity {

    public String transliterate() {
            return getId().toString();
        }

}
