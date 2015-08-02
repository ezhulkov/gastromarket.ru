package org.ohm.gastro.domain;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface PurchaseEntity extends BaseEntity {

    enum Type {
        PRODUCT, OFFER
    }

    Integer getPrice();

    Type getType();

    String getName();

    CatalogEntity getCatalog();

    String getAvatarUrlSmall();

    default boolean equals(Type type, Long id) {
        return getType() == type && id.equals(getId());
    }

}