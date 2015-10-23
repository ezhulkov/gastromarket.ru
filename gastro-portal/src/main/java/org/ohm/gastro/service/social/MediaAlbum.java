package org.ohm.gastro.service.social;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by ezhulkov on 05.04.15.
 */
public class MediaAlbum {

    public static final String DEFAULT_PAGE_NAME = "default";

    private final String name;
    private final String pageName;
    private final String id;

    public MediaAlbum(final String id, final String name, final String pageName) {
        this.name = name;
        this.id = id;
        this.pageName = pageName;
    }

    public String getName() {
        return StringUtils.isEmpty(name) ? "Альбом" : name;
    }

    public String getId() {
        return id;
    }

    public String getPageName() {
        return pageName;
    }

    @Override
    public String toString() {
        return "MediaAlbum{" +
                "name='" + name + '\'' +
                ", pageName='" + pageName + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

}
