package org.ohm.gastro.service.social;

import org.apache.commons.lang.StringUtils;

/**
 * Created by ezhulkov on 05.04.15.
 */
public class MediaAlbum {

    private final String name;
    private final String id;

    public MediaAlbum(final String id, final String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return StringUtils.isEmpty(name) ? "Альбом" : name;
    }

    public String getId() {
        return id;
    }

}
