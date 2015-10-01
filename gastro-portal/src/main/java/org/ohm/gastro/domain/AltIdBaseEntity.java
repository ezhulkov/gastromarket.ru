package org.ohm.gastro.domain;

import org.ohm.gastro.util.CommonsUtils;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Created by ezhulkov on 24.08.14.
 */
@MappedSuperclass
public abstract class AltIdBaseEntity extends AbstractBaseEntity implements AltIdEntity {

    @Column(name = "alt_id")
    private String altId;

    @Column
    private String name;

    @Override
    public String getAltId() {
        return altId == null ? (getId() == null ? null : getId().toString()) : altId;
    }

    public void setAltId(String altId) {
        this.altId = altId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public final String transliterate() {
        return CommonsUtils.transliterate(getName());
    }

}
