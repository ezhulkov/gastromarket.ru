package org.ohm.gastro.domain;

import java.io.Serializable;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface BaseEntity extends Serializable {

    public static final long serialVersionUID = 1L;

    public Long getId();

}
