package org.ohm.gastro.service.social;

/**
 * Created by ezhulkov on 05.04.15.
 */
public class MediaElement {

    private final String link;
    private final String avatarUrl;
    private final String caption;

    public MediaElement(final String link, final String avatarUrl, final String caption) {
        this.link = link;
        this.avatarUrl = avatarUrl;
        this.caption = caption;
    }

    public String getLink() {
        return link;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getCaption() {
        return caption;
    }

}
