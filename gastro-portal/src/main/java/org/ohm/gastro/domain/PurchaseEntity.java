package org.ohm.gastro.domain;

/**
 * Created by ezhulkov on 21.08.14.
 */
public interface PurchaseEntity extends AltIdEntity {

    enum Type {
        PRODUCT, OFFER
    }

    Integer getPrice();

    Type getType();

    String getName();

    CatalogEntity getCatalog();

    String getAvatarUrlSmall();

    String getAvatarUrlMedium();

}