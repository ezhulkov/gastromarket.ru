package org.ohm.gastro.service.social;

/**
 * Created by ezhulkov on 05.04.15.
 */
public class MediaAlbum {

    private final String name;
    private final String id;

    public MediaAlbum(final String name, final String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

}
