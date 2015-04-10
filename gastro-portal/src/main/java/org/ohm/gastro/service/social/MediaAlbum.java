package org.ohm.gastro.service.social;

import org.apache.commons.lang.StringUtils;

/**
 * Created by ezhulkov on 05.04.15.
 */
public class MediaAlbum {

    private final String avatarUrl;
    private final String name;
    private final String id;
    private final int size;

    public MediaAlbum(String avatarUrl, final String name, final String id, int size) {
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.id = id;
        this.size = size;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return StringUtils.isEmpty(name) ? "Альбом" : name;
    }

    public String getId() {
        return id;
    }

}
