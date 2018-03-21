package org.ohm.gastro.domain;

/**
 * Created by ezhulkov on 04.10.15.
 */
public class ImageWithObject<OBJECT extends BaseEntity, IMAGE extends BaseEntity> {

    private final OBJECT object;
    private final IMAGE image;

    public ImageWithObject(final OBJECT object, final IMAGE image) {
        this.object = object;
        this.image = image;
    }

    public OBJECT getObject() {
        return object;
    }

    public IMAGE getImage() {
        return image;
    }

}
