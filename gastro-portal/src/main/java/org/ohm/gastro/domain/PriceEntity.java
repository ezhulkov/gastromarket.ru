package org.ohm.gastro.domain;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface PriceEntity extends BaseEntity {

    enum Type {
        PRODUCT, OFFER
    }

    Integer getPrice();

    Type getType();

    String getName();

}